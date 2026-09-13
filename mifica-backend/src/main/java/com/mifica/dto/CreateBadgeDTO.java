package com.mifica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para requisição de criação de badge no contrato.
 * O cliente (frontend) envia este DTO após assinar a transação.
 */
public class CreateBadgeDTO {

    @JsonProperty("txHash")
    @NotBlank(message = "txHash é obrigatório")
    @Pattern(regexp = "^0x[a-fA-F0-9]{64}$", message = "txHash deve ser um hash válido (0x...)")
    private String txHash;

    @JsonProperty("toAddress")
    @NotBlank(message = "toAddress é obrigatório")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "toAddress deve ser um endereço válido (0x...)")
    private String toAddress;

    @JsonProperty("metadata")
    @NotBlank(message = "metadata é obrigatória")
    private String metadata;

    @JsonProperty("chainId")
    private Long chainId;

    // Constructors
    public CreateBadgeDTO() {}

    public CreateBadgeDTO(String txHash, String toAddress, String metadata, Long chainId) {
        this.txHash = txHash;
        this.toAddress = toAddress;
        this.metadata = metadata;
        this.chainId = chainId;
    }

    // Getters & Setters
    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    @Override
    public String toString() {
        return "CreateBadgeDTO{" +
                "txHash='" + txHash + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", metadata='" + metadata + '\'' +
                ", chainId=" + chainId +
                '}';
    }
}
