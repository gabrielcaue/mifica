package com.mifica.blockchain;

import com.mifica.dto.TransacaoBlockchainDTO;
import com.mifica.dto.TxSubmissionDTO;
import com.mifica.dto.CreateBadgeDTO;
import com.mifica.entity.Role;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de blockchain — registra e consulta transações na blockchain.
 * Persiste transações com hash, remetente, destinatário e valor no banco de dados.
 * Converte entre entidade JPA e DTO para expor via API REST.
 */
@Service
public class BlockchainService {

    private static final double LIMITE_MOVIMENTACAO_ADMIN = 1_000_000.0;

    // ICP-TOTAL: 3
    // Serviço de blockchain com validações de remetente, destinatário, role, limite e regras de permissão.
    // Candidata a refatoração em: BlockchainValidator (extrai toda validação).
    // ICP-01: Serviço faz transformação bidirecional DTO↔entidade com carimbo temporal de transação.

    @Autowired
    private TransacaoBlockchainRepository transacaoRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Web3j web3j;

    @Value("${POLYGON_CHAIN_ID:137}")
    private Integer polygonChainId;

    @Value("${CONFIRMATIONS_REQUIRED:3}")
    private Integer confirmationsRequired;

    @Value("${RPC_POLL_INTERVAL_MS:3000}")
    private Long rpcPollIntervalMs;

    /** Registra uma nova transação blockchain com timestamp automático. */
    public TransacaoBlockchainDTO registrarTransacao(String emailRemetente, String roleRemetente, TransacaoBlockchainDTO dto) {
        // ICP-02: Registro valida perfil do remetente e normaliza a transação antes da persistência.
        if (dto == null) {
            throw new IllegalArgumentException("Dados da transação são obrigatórios.");
        }

        if (dto.getDestinatario() == null || dto.getDestinatario().trim().isEmpty()) {
            throw new IllegalArgumentException("Destinatário é obrigatório.");
        }

        if (dto.getValor() <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }

        Usuario remetente = usuarioRepository.findByEmail(emailRemetente)
            .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado."));

        Usuario destinatario = usuarioRepository.findByEmail(dto.getDestinatario().trim())
            .orElseThrow(() -> new IllegalArgumentException("Destinatário não encontrado."));

        boolean remetenteEhAdmin = Role.ROLE_ADMIN.name().equalsIgnoreCase(roleRemetente)
            || (remetente.getRole() != null && Role.ROLE_ADMIN.equals(remetente.getRole()));
        boolean destinatarioEhAdmin = Role.ROLE_ADMIN.equals(destinatario.getRole());

        if (!remetenteEhAdmin && destinatarioEhAdmin) {
            throw new IllegalArgumentException("Usuários comuns só podem transferir para usuários comuns.");
        }

        if (remetenteEhAdmin) {
            // ICP-03: CDD de limite financeiro do admin é aplicado pelo somatório histórico + valor atual.
            double totalJaMovimentado = transacaoRepo.somarValorMovimentadoPorRemetente(remetente.getEmail());
            double totalAposTransacao = totalJaMovimentado + dto.getValor();

            if (totalAposTransacao > LIMITE_MOVIMENTACAO_ADMIN) {
                double saldoDisponivel = Math.max(0, LIMITE_MOVIMENTACAO_ADMIN - totalJaMovimentado);
                throw new IllegalArgumentException(
                    String.format(
                        "Limite de movimentação do admin (%.2f) excedido. Saldo disponível: %.2f.",
                        LIMITE_MOVIMENTACAO_ADMIN,
                        saldoDisponivel
                    )
                );
            }
        }

        TransacaoBlockchain transacao = new TransacaoBlockchain();
        transacao.setHashTransacao(dto.getHashTransacao() == null || dto.getHashTransacao().isBlank()
            ? UUID.randomUUID().toString()
            : dto.getHashTransacao());
        transacao.setRemetente(remetente.getEmail());
        transacao.setDestinatario(destinatario.getEmail());
        transacao.setValor(dto.getValor());
        transacao.setDataTransacao(LocalDateTime.now());

        TransacaoBlockchain salva = transacaoRepo.save(transacao);
        return toDTO(salva);
    }

    /**
     * Registrar transação real recebida do frontend (após MetaMask enviar e retornar txHash).
     * Faz polling do receipt até ter status=1 e número mínimo de confirmações antes de persistir.
     */
    public TransacaoBlockchainDTO registrarTransacaoPorTxHash(String emailRemetente, String roleRemetente, TxSubmissionDTO dto) throws RuntimeException {
        if (dto == null) throw new IllegalArgumentException("Dados da transação são obrigatórios.");

        if (dto.getChainId() == null || !dto.getChainId().equals(polygonChainId)) {
            throw new IllegalArgumentException("ChainId inválido. Esperado: " + polygonChainId);
        }

        if (dto.getTxHash() == null || dto.getTxHash().isBlank()) {
            throw new IllegalArgumentException("txHash é obrigatório.");
        }

        // Idempotência: não persistir tx duplicada
        Optional<TransacaoBlockchain> existente = transacaoRepo.findByHashTransacao(dto.getTxHash());
        if (existente.isPresent()) {
            return toDTO(existente.get());
        }

        // Polling de receipt
        TransactionReceipt receipt = null;
        int attempts = 0;
        int maxAttempts = 60; // safety cap (~3min at 3s interval)
        try {
            while (attempts < maxAttempts) {
                EthGetTransactionReceipt ethReceipt = web3j.ethGetTransactionReceipt(dto.getTxHash()).send();
                if (ethReceipt != null && ethReceipt.getTransactionReceipt().isPresent()) {
                    receipt = ethReceipt.getTransactionReceipt().get();
                    break;
                }
                attempts++;
                Thread.sleep(rpcPollIntervalMs);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (IOException ioe) {
            throw new RuntimeException("Erro ao consultar RPC: " + ioe.getMessage(), ioe);
        }

        if (receipt == null) {
            throw new RuntimeException("Receipt não encontrado para txHash: " + dto.getTxHash());
        }

        // Verificar status
        if (receipt.getStatus() == null || !(receipt.getStatus().equals("0x1") || receipt.getStatus().equals("1"))) {
            throw new RuntimeException("Transação falhou na chain (status != 1).");
        }

        // Esperar confirmações
        try {
            int confirmAttempts = 0;
            while (confirmAttempts < maxAttempts) {
                EthBlock latest = web3j.ethGetBlockByNumber(org.web3j.protocol.core.DefaultBlockParameterName.LATEST, false).send();
                BigInteger latestNumber = latest.getBlock().getNumber();
                BigInteger txBlock = receipt.getBlockNumber();
                if (latestNumber.subtract(txBlock).compareTo(BigInteger.valueOf(confirmationsRequired)) >= 0) {
                    break;
                }
                confirmAttempts++;
                Thread.sleep(rpcPollIntervalMs);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Build entity from submitted data and receipt
        TransacaoBlockchain transacao = new TransacaoBlockchain();
        transacao.setHashTransacao(dto.getTxHash());
        transacao.setRemetente(dto.getFrom());
        transacao.setDestinatario(dto.getTo());
        // value is provided by frontend in ETH; keep double as before for compatibility
        transacao.setValor(dto.getValue() != null ? dto.getValue() : 0.0);
        transacao.setDataTransacao(LocalDateTime.now());

        TransacaoBlockchain salva = transacaoRepo.save(transacao);
        return toDTO(salva);
    }

    public List<TransacaoBlockchainDTO> listarTransacoes() {
        return transacaoRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Registra um badge criado via contrato inteligente.
     * Valida a transação, aguarda confirmações e persiste no banco de dados.
     * 
     * @param dto com txHash, toAddress, metadata, chainId
     * @return DTO com hash e status
     * @throws IllegalArgumentException se validação falhar
     */
    public TransacaoBlockchainDTO registrarBadge(CreateBadgeDTO dto) {
        // Validar chain ID
        if (!dto.getChainId().equals(137L)) {
            throw new IllegalArgumentException("Badge contrato está em Polygon (chainId=137). Recebido: " + dto.getChainId());
        }

        String txHash = dto.getTxHash();
        String toAddress = dto.getToAddress();

        // Checar idempotência
        Optional<TransacaoBlockchain> existente = transacaoRepo.findByHashTransacao(txHash);
        if (existente.isPresent()) {
            return toDTO(existente.get());
        }

        // Aguardar confirmações
        try {
            Optional<TransactionReceipt> receiptOpt = Optional.empty();
            int maxAttempts = 60;  // 60 tentativas = ~5 minutos com 5s de delay
            for (int i = 0; i < maxAttempts; i++) {
                EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
                if (receipt.getTransactionReceipt().isPresent()) {
                    receiptOpt = receipt.getTransactionReceipt();
                    break;
                }
                Thread.sleep(5000);  // Aguardar 5 segundos
            }

            if (receiptOpt.isEmpty()) {
                throw new IOException("Transação não confirmada dentro do tempo limite");
            }

            TransactionReceipt receipt = receiptOpt.get();

            // Validar status
            if (!receipt.isStatusOK()) {
                throw new IOException("Transação falhou na blockchain (status=0)");
            }

            // Registrar no banco
            TransacaoBlockchain transacao = new TransacaoBlockchain();
            transacao.setHashTransacao(txHash);
            transacao.setRemetente(receipt.getFrom());
            transacao.setDestinatario(toAddress);
            transacao.setValor(0.0);  // Badges não têm valor, apenas evento
            transacao.setDataTransacao(LocalDateTime.now());

            TransacaoBlockchain salva = transacaoRepo.save(transacao);
            return toDTO(salva);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupção ao registrar badge: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao registrar badge: " + e.getMessage(), e);
        }
    }

    private TransacaoBlockchainDTO toDTO(TransacaoBlockchain transacao) {
        TransacaoBlockchainDTO dto = new TransacaoBlockchainDTO();
        dto.setId(transacao.getId());
        dto.setHashTransacao(transacao.getHashTransacao());
        dto.setRemetente(transacao.getRemetente());
        dto.setDestinatario(transacao.getDestinatario());
        dto.setValor(transacao.getValor());
        dto.setDataTransacao(transacao.getDataTransacao());
        return dto;
    }
}
