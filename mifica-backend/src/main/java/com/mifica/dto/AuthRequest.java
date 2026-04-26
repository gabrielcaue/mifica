package com.mifica.dto;

// ICP-TOTAL: 0
// DTO trivial: Apenas container de dados (email + senha) para request de autenticação.
public class AuthRequest {
    private String email;
    private String senha;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
