package com.mifica.dto;

import com.mifica.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UsuarioDTO {

    // ICP-TOTAL: 2
    // ICP-01: DTO agrega validações de entrada e múltiplos perfis de construção (manual e por entidade).

    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @Min(value = 1, message = "Reputação mínima é 1.")
    @Max(value = 5, message = "Reputação máxima é 5.")
    private Integer reputacao;

    private String nivel;

    @NotNull(message = "Data de nascimento é obrigatória.")
    private LocalDate dataNascimento;

    @NotBlank(message = "Telefone é obrigatório.")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos.")
    private String telefone;

    @NotBlank(message = "Papel do usuário é obrigatório.")
    private String role; // ✅ agora é String

    // 🔹 Construtor padrão
    public UsuarioDTO() {}

    // 🔹 Construtor completo
    public UsuarioDTO(Long id, String nome, String email, String senha, Integer reputacao, String nivel,
                      LocalDate dataNascimento, String telefone, String role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.reputacao = reputacao;
        this.nivel = nivel;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.role = role;
    }

    // 🔹 Construtor baseado na entidade
    public UsuarioDTO(Usuario usuario) {
        // ICP-02: Conversão de entidade inclui normalização de papel enum para string de transporte.
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.reputacao = usuario.getReputacao();
        this.nivel = usuario.getNivel();
        this.dataNascimento = usuario.getDataNascimento();
        this.telefone = usuario.getTelefone();
        this.role = usuario.getRole() != null ? usuario.getRole().name() : null; // ✅ converte enum → String
        // senha omitida por segurança
    }

    // 🔹 Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Integer getReputacao() { return reputacao; }
    public void setReputacao(Integer reputacao) { this.reputacao = reputacao; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
