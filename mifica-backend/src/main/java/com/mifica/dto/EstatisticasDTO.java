package com.mifica.dto;

public class EstatisticasDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO consolida indicadores agregados para consumo direto pelo frontend.
    private int totalUsuarios;
    private double mediaReputacao;

    public EstatisticasDTO(int totalUsuarios, double mediaReputacao) {
        this.totalUsuarios = totalUsuarios;
        this.mediaReputacao = mediaReputacao;
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public double getMediaReputacao() {
        return mediaReputacao;
    }

    public void setMediaReputacao(double mediaReputacao) {
        this.mediaReputacao = mediaReputacao;
    }
}
