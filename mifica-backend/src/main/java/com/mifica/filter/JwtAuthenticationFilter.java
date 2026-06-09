package com.mifica.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mifica.util.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // ICP-TOTAL: 2
    // ICP-01: Filtro extrai token bearer e injeta autenticação no contexto de segurança.

    /**
     * Observação importante:
     * Esta classe agora é um componente Spring e injeta `JwtService` via construtor.
     * Contudo, atualmente ela NÃO é registrada na cadeia de filtros definida em
     * `SecurityConfig` — o projeto utiliza o filtro `JwtFiltro` (bean) na configuração
     * de segurança. Manter esta classe sem registro pode confundir a manutenção.
     *
     * Opções recomendadas:
     * - Registrar explicitamente este filtro em `SecurityConfig.filterChain(...)`
     *   (por exemplo: `http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`),
     * - OU remover/arquivar esta classe se não for necessária.
     */

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // ICP-02: Fluxo combina validação de token e fallback de resposta 401 para credenciais inválidas.

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = jwtService.validarToken(token);

                String email = claims.getSubject();

                // Aqui você pode configurar o contexto de autenticação
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(email, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}