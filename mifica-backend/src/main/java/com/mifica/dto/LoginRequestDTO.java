package com.mifica.dto;

public class LoginRequestDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO define contrato de entrada para endpoint de login autenticado.
    private String email;
    private String senha;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
