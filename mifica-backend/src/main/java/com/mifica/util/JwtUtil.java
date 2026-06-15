package com.mifica.util;

import org.springframework.stereotype.Component;

/**
 * Fachada de compatibilidade para pontos do sistema que ainda injetam JwtUtil.
 * Toda a lógica real fica centralizada em JwtService.
 */
@Component
public class JwtUtil {

    // ICP-TOTAL: 3
    // Componente de JWT com geração, extração e validação de token com cifragem/decifragem.
    // ICP-01: Componente centraliza geração, extração e validação de JWT com dependência de estado externo (secret + repositório).

    private final JwtService jwtService;

    public JwtUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /** Gera token JWT para o usuário identificado pelo email. */
    public String gerarToken(String email) {
        return jwtService.gerarToken(email);
    }

    /** Extrai o email (subject) do token. */
    public String extrairEmail(String token) {
        return jwtService.extrairEmail(token);
    }

    /** Extrai a role do token. */
    public String extrairRole(String token) {
        return jwtService.extrairRole(token);
    }

    /** Verifica se o token é válido. */
    public boolean tokenValido(String token) {
        return jwtService.tokenValido(token);
    }

    /** Verifica se o token está expirado. */
    public boolean tokenExpirado(String token) {
        return jwtService.tokenExpirado(token);
    }
}
