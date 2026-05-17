package com.mifica.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuração de teste para desabilitar segurança
 * Permite que testes de integração rodem sem problemas com Spring Security
 */
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {
    
    /**
     * Fornece BCryptPasswordEncoder para testes
     * Mesmo que segurança esteja desabilitada, alguns serviços podem precisar
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
