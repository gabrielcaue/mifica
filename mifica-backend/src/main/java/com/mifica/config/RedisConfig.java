package com.mifica.config;

import com.mifica.redis.GamificationSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.lang.NonNull;

/**
 * Configuração do Redis Pub/Sub para comunicação assíncrona.
 *
 * Arquitetura: Publisher → Canal Redis → Subscriber
 * - GamificationPublisher publica eventos no canal "gamification-events"
 * - RedisMessageListenerContainer escuta o canal e delega ao GamificationSubscriber
 * - Conexão com Redis (Upstash em produção, Docker local em dev) via spring.data.redis.*
 */
@Configuration
public class RedisConfig {

    // ICP-TOTAL: 3
    // ICP-01: Configuração define infraestrutura Pub/Sub e tolerância a falha de conexão no startup.

    /** Nome do canal Redis Pub/Sub usado para eventos de gamificação. */
    public static final String GAMIFICATION_CHANNEL = "gamification-events";

    /** Template para operações Redis com serialização de Strings (publish/subscribe). */
    @Bean
    public StringRedisTemplate stringRedisTemplate(@NonNull RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /** Define o tópico (canal) Redis que será usado para publicar e escutar eventos. */
    @Bean
    public ChannelTopic gamificationTopic() {
        return new ChannelTopic(GAMIFICATION_CHANNEL);
    }

    /**
     * Adapter que conecta o subscriber ao listener container.
     * Quando uma mensagem chega no canal, o método "onMessage" do subscriber é invocado.
     */
    @Bean
    public MessageListenerAdapter gamificationListenerAdapter(@NonNull GamificationSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    /**
     * Container que gerencia o ciclo de vida do listener Redis Pub/Sub.
     * Conecta-se ao Redis, se inscreve no canal e despacha mensagens para o adapter.
     * Equivale ao @KafkaListener do Kafka, mas para Redis Pub/Sub.
     */
    /**
     * Container resiliente: se o Redis estiver indisponível no startup,
     * a aplicação inicia normalmente — apenas o Pub/Sub fica inativo.
    * Isso evita crash loops no PaaS quando o Upstash estiver temporariamente fora.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @NonNull RedisConnectionFactory connectionFactory,
            @NonNull MessageListenerAdapter gamificationListenerAdapter,
            @NonNull ChannelTopic gamificationTopic) {

        // ICP-02: Subclasse anônima customiza lifecycle para permitir startup degradado sem derrubar a aplicação.

        final Logger log = LoggerFactory.getLogger(RedisConfig.class);

        RedisMessageListenerContainer container = new RedisMessageListenerContainer() {
            @Override
            public void start() {
                try {
                    super.start();
                    log.info("✅ Redis Pub/Sub conectado com sucesso no canal '{}'", GAMIFICATION_CHANNEL);
                } catch (Exception e) {
                    log.warn("⚠️ Redis Pub/Sub indisponível — app continua sem listener: {}", e.getMessage());
                }
            }
        };
        // Conecta ao Redis (host/port/password definidos em application.properties)
        container.setConnectionFactory(connectionFactory);
        // ICP-03: Encadeamento explícito de listener e tópico define o roteamento efetivo de eventos.
        // Registra o listener no canal de gamificação
        container.addMessageListener(gamificationListenerAdapter, gamificationTopic);
        return container;
    }
}
