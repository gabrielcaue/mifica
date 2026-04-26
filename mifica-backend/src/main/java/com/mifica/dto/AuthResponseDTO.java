package com.mifica.dto;

// ICP-TOTAL: 0
// DTO trivial: Apenas container para token JWT no response.
public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
