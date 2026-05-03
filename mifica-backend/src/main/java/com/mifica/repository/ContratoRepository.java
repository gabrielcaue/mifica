package com.mifica.repository;

import com.mifica.entity.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe persistência CRUD para Contrato.
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
}
