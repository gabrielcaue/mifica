package com.mifica.blockchain;

import com.mifica.dto.TransacaoBlockchainDTO;
import com.mifica.entity.Role;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    // ICP-01: Serviço faz transformação bidirecional DTO↔entidade com carimbo temporal de transação.

    @Autowired
    private TransacaoBlockchainRepository transacaoRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public List<TransacaoBlockchainDTO> listarTransacoes() {
        return transacaoRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
