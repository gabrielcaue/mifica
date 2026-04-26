package com.mifica.redis;

import com.mifica.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Publisher Redis Pub/Sub — publica eventos de gamificação no canal "gamification-events".
 *
 * Responsabilidade: receber chamadas dos controllers e publicar mensagens
 * no canal Redis para processamento assíncrono pelo GamificationSubscriber.
 *
 * Fluxo: Controller → Publisher → Redis Canal → Subscriber → GamificationService
 */
@Service
public class GamificationPublisher {

    // ICP-TOTAL: 3
    // Classe simples: Formatação de mensagem + publicação + tratamento de erro assíncrono.
    // ICP-01: Publisher desacopla operação de usuário do processamento de gamificação via evento assíncrono.

    private static final Logger log = LoggerFactory.getLogger(GamificationPublisher.class);

    private final StringRedisTemplate redisTemplate;

    public GamificationPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Publica um evento de pontos no canal Redis Pub/Sub.
     * O GamificationSubscriber receberá a mensagem e processará os pontos.
     *
     * @param userId ID do usuário que receberá os pontos
     * @param points quantidade de pontos a adicionar
     */
    public void publishEvent(Long userId, int points) {
        // Formato padronizado: "User:{id} Points:{pontos}" — parseado pelo subscriber
        String message = "User:" + userId + " Points:" + points;
        try {
            // convertAndSend publica a mensagem no canal Redis (fire-and-forget)
            redisTemplate.convertAndSend(RedisConfig.GAMIFICATION_CHANNEL, message);
            log.info("📤 Evento publicado no Redis: {}", message);
        } catch (Exception e) {
            // Redis indisponível não deve impedir operações do usuário
            log.warn("⚠️ Falha ao publicar evento no Redis (operação do usuário não afetada): {}", e.getMessage());
        }
    }
}
