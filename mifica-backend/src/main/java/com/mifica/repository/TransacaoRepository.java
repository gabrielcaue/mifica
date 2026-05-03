package com.mifica.repository;

import com.mifica.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ICP-TOTAL: 0
// Interface de repositório Spring Data: apenas expõe persistência CRUD para Transacao.
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByRemetenteOrDestinatario(String remetente, String destinatario);
}
