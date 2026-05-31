package com.mifica.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Guardrail #7: Auditoria de segurança para endpoints sensíveis.
 *
 * Regras:
 * - Não registra payload nem token JWT.
 * - Registra apenas metadados: endpoint, método, status, usuário, IP e duração.
 */
@Component
public class SecurityAuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditFilter.class);

    private static final List<String> SENSITIVE_PATH_PREFIXES = List.of(
            "/api/usuarios/login",
            "/api/usuarios/cadastro",
            "/api/usuarios/cadastro-admin",
            "/api/usuarios/validar-acesso-admin",
            "/api/admin/",
            "/api/transacoes/",
            "/api/blockchain/"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return SENSITIVE_PATH_PREFIXES.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long elapsedMs = System.currentTimeMillis() - start;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";

        String clientIp = extractClientIp(request);
        String event = buildEventName(request.getMethod(), request.getRequestURI());

        log.info("AUDIT event={} method={} path={} status={} user={} ip={} durationMs={}",
                event,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                user,
                clientIp,
                elapsedMs);
    }

    private String buildEventName(String method, String path) {
        if (path.startsWith("/api/usuarios/login")) return "AUTH_LOGIN";
        if (path.startsWith("/api/usuarios/cadastro-admin")) return "AUTH_REGISTER_ADMIN";
        if (path.startsWith("/api/usuarios/cadastro")) return "AUTH_REGISTER_USER";
        if (path.startsWith("/api/admin/")) return "ADMIN_OPERATION";
        if (path.startsWith("/api/transacoes/")) return "TRANSACTION_OPERATION";
        if (path.startsWith("/api/blockchain/")) return "BLOCKCHAIN_OPERATION";
        return method + "_SENSITIVE_OPERATION";
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
