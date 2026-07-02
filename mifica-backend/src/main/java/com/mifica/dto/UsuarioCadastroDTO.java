package com.mifica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioCadastroDTO {

    // ICP-TOTAL: 1
    // ICP-01: DTO especializa dados de onboarding para criação de conta.

    @NotBlank(message = "Nome completo é obrigatório.")
    private String nome;

    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
    private String senha;

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

}
