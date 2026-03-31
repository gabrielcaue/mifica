package com.mifica.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao", indexes = {
    @Index(name = "idx_avaliacao_avaliador", columnList = "avaliador_id"),
    @Index(name = "idx_avaliacao_avaliado", columnList = "avaliado_id"),
    @Index(name = "idx_avaliacao_data", columnList = "data")
})
public class Avaliacao {

    // ICP-TOTAL: 1
    // ICP-01: Entidade representa vínculo de avaliação entre usuários e reputação percebida.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int nota; // de 1 a 5

    @Column(length = 500)
    private String comentario;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name = "avaliador_id")
    private Usuario avaliador;

    @ManyToOne
    @JoinColumn(name = "avaliado_id")
    private Usuario avaliado;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Usuario getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(Usuario avaliador) {
        this.avaliador = avaliador;
    }

    public Usuario getAvaliado() {
        return avaliado;
    }

    public void setAvaliado(Usuario avaliado) {
        this.avaliado = avaliado;
    }
}
