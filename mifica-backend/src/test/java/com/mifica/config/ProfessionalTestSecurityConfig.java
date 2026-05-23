package com.mifica.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Configuração profissional de segurança para testes de integração.
 * 
 * Objetivo: Manter Spring Security ativo mas com configuração relaxada para testes
 * - Desabilita apenas CSRF (desnecessário em testes stateless)
 * - Mantém autenticação e autorização funcionando
 * - Permite URLs públicas conforme definido
 * - Usa @WithMockUser em testes que precisam autenticação
 */
@TestConfiguration
@Profile("test")
public class ProfessionalTestSecurityConfig {

    /**
     * SecurityFilterChain para teste: versão simplificada de SecurityConfig
     * Mantém a segurança mas sem complexidade de CORS/OAuth2 production
     */
    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF desabilitado (seguro em testes stateless com JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Headers simplificado para testes
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())  // Permite H2 console
                .xssProtection(xss -> xss.disable())
            )
            
            // Autorização: mantém estrutura original mas relaxada para testes
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos testáveis
                .requestMatchers(
                    "/",
                    "/api/usuarios/login",
                    "/api/usuarios/cadastro",
                    "/api/usuarios/validar-acesso",
                    "/api/blockchain/**",
                    "/h2-console/**",
                    "/actuator/**"
                ).permitAll()
                
                // Transações requerem autenticação
                .requestMatchers("/api/transacoes/**").authenticated()
                
                // Admin requer role ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Gamification requer autenticação
                .requestMatchers("/api/gamification/**").authenticated()
                
                // Reputação requer autenticação
                .requestMatchers("/api/reputacao/**").authenticated()
                
                // Tudo mais requer autenticação
                .anyRequest().authenticated()
            )
            
            // Autenticação HTTP basic para testes (alternativa ao JWT)
            .httpBasic(httpBasic -> {})
            
            .build();
        
        return http.build();
    }

    /**
     * BCryptPasswordEncoder: necessário para autenticação com @WithMockUser
     */
    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
