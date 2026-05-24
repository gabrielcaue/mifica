package com.mifica.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração de segurança para testes.
 * Fornece apenas os beans necessários para testes funcionarem com @WithMockUser.
 * O SecurityFilterChain padrão é fornecido automaticamente pelo Spring Security
 * quando nenhuma configuração customizada está ativa (devido ao @Profile("!test") no SecurityConfig).
 * 
 * Em testes, todos os endpoints autenticados são permitidos, apenas OAuth2/SAML2 são excluídos.
 */
@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Value("${app.cors.allowed-origin-patterns:*}")
    private String allowedOriginPatterns;

    /**
     * Encoder BCrypt para criptografia de senhas (fator de custo padrão: 10).
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração CORS: origens permitidas são lidas dinamicamente.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
            Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList()
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


