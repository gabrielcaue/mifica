package com.mifica.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_credito", indexes = {
    @Index(name = "idx_solicitacao_status", columnList = "status"),
    @Index(name = "idx_solicitacao_data", columnList = "data_solicitacao"),
    @Index(name = "idx_solicitacao_usuario", columnList = "usuario_id")
})
public class SolicitacaoCredito {

    // ICP-TOTAL: 1
    // ICP-01: Entidade representa ciclo de solicitação de crédito com status e temporalidade.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDate prazoPagamento;

    @Column(nullable = false, length = 40)
    private String status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioSolicitante;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(BigDecimal valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getPrazoPagamento() {
        return prazoPagamento;
    }

    public void setPrazoPagamento(LocalDate prazoPagamento) {
        this.prazoPagamento = prazoPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Usuario getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public LocalDateTime getDataCriacao() {
        return dataSolicitacao;
    }
}
