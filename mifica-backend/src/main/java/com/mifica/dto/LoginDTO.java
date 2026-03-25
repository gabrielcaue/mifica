package com.mifica.dto;

public class LoginDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO transporta credenciais mínimas para autenticação do usuário.
    private String email;
    private String senha;

    public LoginDTO() {
    }

    public LoginDTO(String email, String senha) {
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
