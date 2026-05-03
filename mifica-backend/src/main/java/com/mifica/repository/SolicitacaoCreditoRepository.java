package com.mifica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe consultas de solicitações de crédito.
public interface SolicitacaoCreditoRepository extends JpaRepository<SolicitacaoCredito, Long> {
    List<SolicitacaoCredito> findByUsuarioSolicitante(Usuario usuario);
}
