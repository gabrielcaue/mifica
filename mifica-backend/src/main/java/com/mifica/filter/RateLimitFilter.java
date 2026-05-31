package com.mifica.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Guardrail #3: RateLimitFilter
 *
 * Limita requisições por IP para proteger contra abuso e DoS.
 * Usa Redis para rastreamento de estado compartilhado entre instâncias.
 *
 * Configuração:
 * - 100 requisições por minuto por IP
 * - Responde com 429 (Too Many Requests) quando limite é atingido
 *
 * Benefícios:
 * - Proteção contra ataques de força bruta (login, cadastro).
 * - Proteção contra scraping e abuso de API.
 * - Sem impacto em lógica de negócio.
 * - Configurável por endereço IP do cliente.
 * - Sincronizado entre múltiplas instâncias via Redis.
 *
 * NÃO altera: Lógica de negócio, fluxos de autenticação, processamento de dados.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired(required = false)
    private RedisTemplate<String, Integer> redisTemplate;

    // Configuração: 100 requisições por minuto
    private static final int REQUESTS_PER_MINUTE = 100;
    private static final long WINDOW_SIZE_SECONDS = 60;
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Se Redis não está disponível, permite a requisição (fallback seguro)
        if (redisTemplate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIpAddress(request);
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + clientIp;

        try {
            // Incrementa contador de requisições
            Long countLong = redisTemplate.opsForValue().increment(rateLimitKey);
            int currentCount = (countLong != null) ? countLong.intValue() : 1;

            // Define expiração na primeira requisição
            if (currentCount == 1) {
                redisTemplate.expire(rateLimitKey, WINDOW_SIZE_SECONDS, TimeUnit.SECONDS);
            }

            // Verifica se limite foi excedido
            if (currentCount > REQUESTS_PER_MINUTE) {
                Long ttlLong = redisTemplate.getExpire(rateLimitKey, TimeUnit.SECONDS);
                long ttl = (ttlLong != null && ttlLong > 0) ? ttlLong : 1;

                log.warn("Rate limit excedido para IP: {} — contador: {}, aguarde {} segundos",
                        clientIp, currentCount, ttl);

                response.setStatus(429); // Too Many Requests
                response.setHeader("Retry-After", String.valueOf(ttl));
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"Rate limit excedido\", " +
                                "\"mensagem\": \"Muitas requisições. Aguarde antes de tentar novamente.\", " +
                                "\"retry_after\": " + ttl + "}"
                );
                return;
            }

            // Requisição permitida
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Erro ao processar rate limit", e);
            // Em caso de erro, permite a requisição (fallback seguro)
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Extrai o IP do cliente da requisição, considerando proxies.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For pode conter múltiplos IPs; pega o primeiro
                return ip.split(",")[0].trim();
            }
        }

        // Fallback: IP da conexão direta
        return request.getRemoteAddr();
    }
}
