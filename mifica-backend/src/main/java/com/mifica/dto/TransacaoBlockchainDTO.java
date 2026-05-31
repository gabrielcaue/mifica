package com.mifica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class TransacaoBlockchainDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO descreve evento de transferência em blockchain para persistência e consulta.
    private Long id;
    
    @NotBlank(message = "Hash da transação é obrigatório.")
    @Pattern(regexp = "0x[a-fA-F0-9]{64}", message = "Hash deve ser um hash Ethereum válido (0x + 64 caracteres hexadecimais).")
    private String hashTransacao;
    
    @NotBlank(message = "Endereço do remetente é obrigatório.")
    @Pattern(regexp = "0x[a-fA-F0-9]{40}", message = "Endereço deve ser um endereço Ethereum válido.")
    private String remetente;
    
    @NotBlank(message = "Endereço do destinatário é obrigatório.")
    @Pattern(regexp = "0x[a-fA-F0-9]{40}", message = "Endereço deve ser um endereço Ethereum válido.")
    private String destinatario;
    
    @Positive(message = "Valor da transação deve ser maior que zero.")
    private double valor;
    
    private LocalDateTime dataTransacao;

    // Getters
    public Long getId() {
        return id;
    }

    public String getHashTransacao() {
        return hashTransacao;
    }

    public String getRemetente() {
        return remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setHashTransacao(String hashTransacao) {
        this.hashTransacao = hashTransacao;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
