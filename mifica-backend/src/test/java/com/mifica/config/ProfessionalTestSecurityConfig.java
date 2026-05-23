package com.mifica.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuração MÍNIMA para testes com Spring Security.
 * 
 * ✅ Estratégia profissional:
 * - Fornecer beans necessários para testes
 * - NÃO sobrescrever SecurityFilterChain (causa conflito!)
 * - Usar @WithMockUser nos testes que precisam autenticação
 * - Deixar Spring Security em seu comportamento padrão relaxado via application-test.yml
 */
@TestConfiguration
@Profile("test")
public class ProfessionalTestSecurityConfig {

    /**
     * ✅ BCryptPasswordEncoder: necessário para @WithMockUser funcionarm
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


