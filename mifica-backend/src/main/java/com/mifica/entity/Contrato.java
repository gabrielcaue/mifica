package com.mifica.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "contrato", indexes = {
    @Index(name = "idx_contrato_nome", columnList = "nome")
})
public class Contrato {

    // ICP-TOTAL: 1
    // ICP-01: Entidade persiste metadados de contrato e referência blockchain.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(length = 255)
    private String enderecoBlockchain;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getEnderecoBlockchain() { return enderecoBlockchain; }
    public void setEnderecoBlockchain(String enderecoBlockchain) { this.enderecoBlockchain = enderecoBlockchain; }
}
