package com.mifica.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * ✅ Configuração de teste para desabilitar segurança
 * Permite que testes de integração rodem sem problemas com Spring Security
 * 
 * Execução:
 * - Ativa apenas quando @ActiveProfiles("test") é usado
 * - Fornece beans mínimos necessários para testes
 * - Não carrega OAuth2, SAML2, ou outras complexidades de segurança
 */
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {
    
    /**
     * ✅ Fornece BCryptPasswordEncoder para testes
     * Mesmo com segurança desabilitada, alguns serviços podem precisar
     */
    @Bean
    @ConditionalOnMissingBean(BCryptPasswordEncoder.class)
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
