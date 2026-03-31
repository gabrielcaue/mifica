package com.mifica.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@DynamicUpdate
@Table(name = "transacao", indexes = {
    @Index(name = "idx_transacao_remetente", columnList = "remetente"),
    @Index(name = "idx_transacao_destinatario", columnList = "destinatario"),
    @Index(name = "idx_transacao_data", columnList = "data_transacao")
})
public class Transacao {

    // ICP-TOTAL: 1
    // ICP-01: Entidade guarda dados financeiros de transferência para rastreabilidade transacional.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String remetente;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false)
    private Double valor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataTransacao;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
