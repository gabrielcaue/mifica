package com.mifica.config;

/**
 * Fonte única para rotas públicas da aplicação.
 * Mantém SecurityConfig e JwtFiltro sincronizados.
 */
public final class PublicPaths {

    private PublicPaths() {
    }

    public static final String ROOT = "/";
    public static final String[] PERMIT_ALL = {
            ROOT,
            "/api/usuarios/login",
            "/api/usuarios/cadastro",
            "/api/usuarios/cadastro-admin",
            "/api/usuarios/validar-acesso-admin",
            "/api/blockchain/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    public static boolean isPublic(String path) {
        if (path == null) {
            return false;
        }

        if (ROOT.equals(path)) {
            return true;
        }

        return path.startsWith("/api/usuarios/login")
                || path.startsWith("/api/usuarios/cadastro")
                || path.startsWith("/api/usuarios/cadastro-admin")
                || path.startsWith("/api/usuarios/validar-acesso-admin")
                || path.startsWith("/api/blockchain")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }
}