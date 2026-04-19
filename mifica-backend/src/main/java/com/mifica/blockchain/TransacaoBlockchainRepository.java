package com.mifica.blockchain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoBlockchainRepository extends JpaRepository<TransacaoBlockchain, Long> {
	// ICP-TOTAL: 1
	// ICP-01: Query agregada provê total movimentado por remetente para enforcement de regra financeira.
	@Query("SELECT COALESCE(SUM(t.valor), 0) FROM TransacaoBlockchain t WHERE t.remetente = :remetente")
	double somarValorMovimentadoPorRemetente(@Param("remetente") String remetente);
}
