package com.mifica.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// ICP-TOTAL: 0
// DTO trivial: Apenas container para nova reputação em request de atualização.
public class ReputacaoDTO {
    
    @Min(value = 0, message = "Reputação não pode ser negativa.")
    @Max(value = 1000, message = "Reputação não pode exceder 1000 pontos.")
    private int novaReputacao;

    public int getNovaReputacao() {
        return novaReputacao;
    }

    public void setNovaReputacao(int novaReputacao) {
        this.novaReputacao = novaReputacao;
    }
}
