package com.mifica.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_reputacao", indexes = {
    @Index(name = "idx_histrep_email", columnList = "email_usuario"),
    @Index(name = "idx_histrep_data", columnList = "data_alteracao")
})
public class HistoricoReputacao {

    // ICP-TOTAL: 1
    // ICP-01: Entidade registra trilha de auditoria para variações de reputação.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String emailUsuario;
    private int reputacaoAnterior;
    private int reputacaoNova;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAlteracao;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public int getReputacaoAnterior() { return reputacaoAnterior; }
    public void setReputacaoAnterior(int reputacaoAnterior) { this.reputacaoAnterior = reputacaoAnterior; }

    public int getReputacaoNova() { return reputacaoNova; }
    public void setReputacaoNova(int reputacaoNova) { this.reputacaoNova = reputacaoNova; }

    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime dataAlteracao) { this.dataAlteracao = dataAlteracao; }
}
