package com.mifica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO define contrato de entrada para endpoint de login autenticado.
    
    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória.")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
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
