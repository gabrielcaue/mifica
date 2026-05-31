package com.mifica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class DesafioDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO concentra payload de desafio entre API e serviço.
    private Long id;
    
    @NotBlank(message = "Título do desafio é obrigatório.")
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres.")
    private String titulo;
    
    @NotBlank(message = "Descrição do desafio é obrigatória.")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres.")
    private String descricao;
    
    @Positive(message = "Pontos do desafio devem ser um valor positivo.")
    private int pontos;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }
}
