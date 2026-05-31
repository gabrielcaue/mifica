package com.mifica.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * Guardrail #4 e #6: ResilienceConfig
 *
 * Configura timeouts, circuit breakers e retry policies para proteção
 * contra falhas em integrações externas (Redis, Blockchain, APIs).
 *
 * Benefícios:
 * - Timeouts evitam que threads fiquem presas esperando respostas infinitas.
 * - Circuit breaker protege serviços dependentes de falhas em cascata.
 * - Retry automático com backoff exponencial para erros transitórios.
 * - Sem alterar lógica de negócio — aplicado com @CircuitBreaker, @TimeLimiter.
 *
 * NÃO altera: Lógica de negócio, fluxos de autenticação, processamento de dados.
 */
@Configuration
public class ResilienceConfig {

    private static final Logger log = LoggerFactory.getLogger(ResilienceConfig.class);

    // ============ TIME LIMITER (Timeouts) ============

    /**
     * TimeLimiter para operações gerais (padrão: 5 segundos).
     * Usado em: Requisições HTTP, chamadas de serviço.
     */
    @Bean
    public TimeLimiter timeLimiterGeneral(TimeLimiterRegistry timeLimiterRegistry) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .cancelRunningFuture(true)
                .build();

        return timeLimiterRegistry.timeLimiter("general", config);
    }

    /**
     * TimeLimiter para operações Redis (padrão: 2 segundos).
     * Usado em: Pub/Sub, Cache, Rate Limiting.
     */
    @Bean
    public TimeLimiter timeLimiterRedis(TimeLimiterRegistry timeLimiterRegistry) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(2))
                .cancelRunningFuture(true)
                .build();

        return timeLimiterRegistry.timeLimiter("redis", config);
    }

    /**
     * TimeLimiter para operações de banco de dados (padrão: 10 segundos).
     * Usado em: Queries complexas, transações longas.
     */
    @Bean
    public TimeLimiter timeLimiterDatabase(TimeLimiterRegistry timeLimiterRegistry) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .cancelRunningFuture(true)
                .build();

        return timeLimiterRegistry.timeLimiter("database", config);
    }

    /**
     * TimeLimiter para chamadas a blockchain (padrão: 15 segundos).
     * Usado em: Transações, consultas Web3j.
     */
    @Bean
    public TimeLimiter timeLimiterBlockchain(TimeLimiterRegistry timeLimiterRegistry) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(15))
                .cancelRunningFuture(true)
                .build();

        return timeLimiterRegistry.timeLimiter("blockchain", config);
    }

    // ============ CIRCUIT BREAKER (Proteção contra falhas) ============

    /**
     * CircuitBreaker para Redis (Pub/Sub, Cache).
     * Configuração:
     * - Abre após 5 falhas em 10 requisições (50%)
     * - Tenta recuperar após 10 segundos
     */
    @Bean
    public CircuitBreaker circuitBreakerRedis(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f) // 50% de falha abre o CB
                .slowCallRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .recordExceptions(Exception.class)
                .build();

        return circuitBreakerRegistry.circuitBreaker("redis", config);
    }

    /**
     * CircuitBreaker para Blockchain (Web3j, smart contracts).
     * Configuração:
     * - Abre após 3 falhas em 5 requisições (60%)
     * - Tenta recuperar após 30 segundos
     */
    @Bean
    public CircuitBreaker circuitBreakerBlockchain(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(60.0f)
                .slowCallRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(2)
                .minimumNumberOfCalls(3)
                .recordExceptions(Exception.class)
                .build();

        return circuitBreakerRegistry.circuitBreaker("blockchain", config);
    }

    /**
     * CircuitBreaker para banco de dados.
     * Configuração: Mais tolerante (80% de falha)
     */
    @Bean
    public CircuitBreaker circuitBreakerDatabase(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(80.0f)
                .slowCallRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(5)
                .minimumNumberOfCalls(10)
                .recordExceptions(Exception.class)
                .build();

        return circuitBreakerRegistry.circuitBreaker("database", config);
    }

    // ============ RETRY (Tentativas com backoff) ============

    /**
     * Retry para Redis (até 3 tentativas com espera exponencial).
     */
    @Bean
    public Retry retryRedis(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .intervalFunction(io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff(500, 2))
                .retryExceptions(Exception.class)
                .build();

        return retryRegistry.retry("redis", config);
    }

    /**
     * Retry para Blockchain (até 2 tentativas).
     */
    @Bean
    public Retry retryBlockchain(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(1000))
                .retryExceptions(Exception.class)
                .build();

        return retryRegistry.retry("blockchain", config);
    }

    // ============ EVENT LISTENERS ============

    /**
     * Log de eventos do CircuitBreaker para observabilidade.
     */
    @Bean
    public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer() {
        return new RegistryEventConsumer<CircuitBreaker>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                CircuitBreaker circuitBreaker = entryAddedEvent.getAddedEntry();
                circuitBreaker.getEventPublisher()
                        .onStateTransition(event -> log.warn(
                                "CircuitBreaker {} transitioned from {} to {}",
                                event.getCircuitBreakerName(),
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()
                        ))
                        .onError(event -> log.error(
                                "CircuitBreaker {} recorded error: {}",
                                event.getCircuitBreakerName(),
                                event.getThrowable().getMessage()
                        ));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemovedEvent) {
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
            }
        };
    }
}
