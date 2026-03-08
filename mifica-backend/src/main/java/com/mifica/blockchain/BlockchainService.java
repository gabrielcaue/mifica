package com.mifica.blockchain;

import com.mifica.dto.TransacaoBlockchainDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de blockchain — registra e consulta transações na blockchain.
 * Persiste transações com hash, remetente, destinatário e valor no banco de dados.
 * Converte entre entidade JPA e DTO para expor via API REST.
 */
@Service
public class BlockchainService {

    @Autowired
    private TransacaoBlockchainRepository transacaoRepo;

    /** Registra uma nova transação blockchain com timestamp automático. */
    public TransacaoBlockchainDTO registrarTransacao(TransacaoBlockchainDTO dto) {
        TransacaoBlockchain transacao = new TransacaoBlockchain();
        transacao.setHashTransacao(dto.getHashTransacao());
        transacao.setRemetente(dto.getRemetente());
        transacao.setDestinatario(dto.getDestinatario());
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
