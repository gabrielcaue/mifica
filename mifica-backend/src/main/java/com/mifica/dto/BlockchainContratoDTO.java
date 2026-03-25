package com.mifica.dto;

public class BlockchainContratoDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO padroniza contrato de dados para integração de camada blockchain.
    private String nomeContrato;
    private String enderecoContrato;

    public String getNomeContrato() {
        return nomeContrato;
    }

    public void setNomeContrato(String nomeContrato) {
        this.nomeContrato = nomeContrato;
    }

    public String getEnderecoContrato() {
        return enderecoContrato;
    }

    public void setEnderecoContrato(String enderecoContrato) {
        this.enderecoContrato = enderecoContrato;
    }
}
