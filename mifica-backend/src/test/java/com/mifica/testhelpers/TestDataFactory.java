package com.mifica.testhelpers;

import com.mifica.entity.User;
import com.mifica.entity.Badge;
import com.mifica.entity.Usuario;
import java.time.LocalDateTime;

/**
 * TestDataFactory - Constrói objetos de teste reutilizáveis
 * Evita duplicação de código de setup entre testes
 */
public class TestDataFactory {

    /**
     * Builder para User com valores padrão
     */
    public static class UserBuilder {
        private Long id = 1L;
        private String name = "Test User";
        private int points = 0;
        private int level = 1;

        public UserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder withPoints(int points) {
            this.points = points;
            return this;
        }

        public UserBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPoints(points);
            user.setLevel(level);
            return user;
        }
    }

    public static UserBuilder userBuilder() {
        return new UserBuilder();
    }

    public static User defaultUser() {
        return userBuilder().build();
    }

    /**
     * Builder para Badge
     */
    public static class BadgeBuilder {
        private Long id = 1L;
        private String name = "Achievement";
        private String description = "Test Badge";
        private Long userId = 1L;

        public BadgeBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BadgeBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public BadgeBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Badge build() {
            Badge badge = new Badge();
            badge.setId(id);
            badge.setName(name);
            badge.setDescription(description);
            badge.setUserId(userId);
            return badge;
        }
    }

    public static BadgeBuilder badgeBuilder() {
        return new BadgeBuilder();
    }

    /**
     * Builder para Usuario (UsuarioService)
     */
    public static class UsuarioBuilder {
        private Long id = 1L;
        private String nome = "João Silva";
        private String email = "joao@example.com";
        private String senha = "senha123";
        private int reputacao = 1;
        private int nivel = 1;
        private String role = "USER";
        private Boolean enabled = true;

        public UsuarioBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UsuarioBuilder withReputacao(int reputacao) {
            this.reputacao = reputacao;
            return this;
        }

        public UsuarioBuilder withRole(String role) {
            this.role = role;
            return this;
        }

        public Usuario build() {
            Usuario usuario = new Usuario();
            usuario.setId(id);
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setReputacao(reputacao);
            usuario.setNivel(nivel);
            usuario.setRole(role);
            usuario.setEnabled(enabled);
            return usuario;
        }
    }

    public static UsuarioBuilder usuarioBuilder() {
        return new UsuarioBuilder();
    }
}
