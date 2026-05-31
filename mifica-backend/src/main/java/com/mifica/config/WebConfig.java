package com.mifica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.Validator;

/**
 * Guardrail #2 (complementar): WebConfig
 *
 * Ativa validação de constrants em nível global.
 * Permite que @Valid e @Validated funcionem em todos os endpoints.
 *
 * Benefícios:
 * - Validação automática de DTOs via @Valid em @RequestBody.
 * - Validação de parâmetros de método com @Validated.
 * - Integração perfeita com GlobalExceptionHandler para feedback consistente.
 */
@Configuration
public class WebConfig {

    /**
     * Bean que valida as constraints definidas nos DTOs.
     */
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Habilita validação de métodos usando @Validated em nível de classe.
     * Permite validar parâmetros em qualquer método Spring-managed.
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
