package com.mifica.repository;

import com.mifica.entity.DesafioGamificado;
import org.springframework.data.jpa.repository.JpaRepository;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe persistência CRUD para DesafioGamificado.
public interface DesafioRepository extends JpaRepository<DesafioGamificado, Long> {
}
