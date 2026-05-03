package com.mifica.repository;

import com.mifica.entity.HistoricoReputacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe consultas e persistência de histórico de reputação.
public interface HistoricoReputacaoRepository extends JpaRepository<HistoricoReputacao, Long> {
    List<HistoricoReputacao> findByEmailUsuario(String email);
}
