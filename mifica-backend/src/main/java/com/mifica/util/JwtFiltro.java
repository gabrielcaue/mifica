package com.mifica.util;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

/**
 * Filtro JWT customizado que intercepta cada requisição HTTP.
 * Extrai o token do header Authorization, valida a assinatura HMAC-SHA256,
 * e injeta o usuário autenticado no SecurityContext do Spring Security.
 *
 * Executa uma única vez por requisição (OncePerRequestFilter).
 * Rotas públicas (login, Swagger, blockchain) são ignoradas via shouldNotFilter.
 */
@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFiltro(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Define quais rotas NÃO passam pelo filtro JWT (endpoints públicos).
     * Essas rotas são acessíveis sem token de autenticação.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
    return path.equals("/") ||
           path.equals("/api/usuarios/login") ||
            path.equals("/api/usuarios/cadastro") ||
            path.equals("/api/usuarios/cadastro-admin") ||
            path.equals("/api/usuarios/validar-acesso-admin") ||
           path.equals("/api/usuarios/criar") ||
           path.startsWith("/swagger-ui") ||
           path.startsWith("/v3/api-docs") ||
           path.startsWith("/api/auth") ||
           path.startsWith("/api/blockchain");
}

    /**
     * Intercepta a requisição, extrai e valida o token JWT.
     * Se válido, injeta email + role no SecurityContext para que
     * o Spring Security autorize o acesso aos endpoints protegidos.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {

        // Extrai o header Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Remove o prefixo "Bearer " para obter apenas o token JWT
            String token = authHeader.substring(7);

            try {
                // Valida assinatura HMAC-SHA256 e extrai os claims (payload)
                Claims claims = jwtService.validarToken(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if (email != null && role != null) {
                    // Cria a authority com prefixo ROLE_ para o Spring Security
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    // Injeta o usuário autenticado no contexto de segurança
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(authority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // Token inválido ou expirado — segue sem autenticação (retornará 403)
            }
        }

        filterChain.doFilter(request, response);
    }
}

