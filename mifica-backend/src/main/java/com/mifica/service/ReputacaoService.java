package com.mifica.service;

import com.mifica.dto.HistoricoReputacaoDTO;
import com.mifica.entity.HistoricoReputacao;
import com.mifica.entity.Usuario;
import com.mifica.repository.HistoricoReputacaoRepository;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReputacaoService {

    // ICP-TOTAL: 2
    // Serviço de reputação que registra alterações e consulta histórico com transformação DTO.
    // ICP-01: Serviço sincroniza histórico de reputação com atualização da entidade principal.

    @Autowired
    private HistoricoReputacaoRepository historicoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public boolean registrarAlteracao(String email, int novaReputacao) {
        // ICP-02: Fluxo condicional preserva atomicidade lógica entre auditoria e reputação atual.
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        HistoricoReputacao historico = new HistoricoReputacao();
        historico.setEmailUsuario(email);
        historico.setReputacaoAnterior(usuario.getReputacao());
        historico.setReputacaoNova(novaReputacao);
        historico.setDataAlteracao(LocalDateTime.now());

        historicoRepo.save(historico);

        usuario.setReputacao(novaReputacao);
        usuarioRepo.save(usuario);
        return true;
    }

    public List<HistoricoReputacaoDTO> listarHistorico(String email) {
        return historicoRepo.findByEmailUsuario(email).stream().map(h -> {
            HistoricoReputacaoDTO dto = new HistoricoReputacaoDTO();
            dto.setId(h.getId());
            dto.setEmailUsuario(h.getEmailUsuario());
            dto.setReputacaoAnterior(h.getReputacaoAnterior());
            dto.setReputacaoNova(h.getReputacaoNova());
            dto.setDataAlteracao(h.getDataAlteracao());
            return dto;
        }).collect(Collectors.toList());
    }
}
