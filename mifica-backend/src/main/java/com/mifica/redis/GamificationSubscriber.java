package com.mifica.redis;

import com.mifica.service.GamificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Subscriber Redis Pub/Sub para eventos de gamificação.
 * Processa mensagens do canal "gamification-events" e delega ao GamificationService.
 *
 * A senha admin.redis.senha é usada para proteger o endpoint REST de acesso
 * ao conteúdo do consumer (via SecureController), não para filtrar mensagens
 * no subscriber — o Pub/Sub do Redis já é protegido pela autenticação do próprio Redis.
 */
@Component
public class GamificationSubscriber {

    private static final Logger log = LoggerFactory.getLogger(GamificationSubscriber.class);

    private final GamificationService gamificationService;

    public GamificationSubscriber(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    /**
     * Chamado automaticamente pelo RedisMessageListenerContainer quando uma mensagem
     * é publicada no canal "gamification-events".
     * Formato esperado: "User:{userId} Points:{points}"
     */
    public void onMessage(String message) {
        log.info("📥 Evento de gamificação recebido: {}", message);

        try {
            // Parse: "User:123 Points:50"
            String[] parts = message.split(" ");
            Long userId = Long.parseLong(parts[0].split(":")[1]);
            int points = Integer.parseInt(parts[1].split(":")[1]);

            gamificationService.addPoints(userId, points);
            log.info("✅ Pontos processados: userId={}, points={}", userId, points);
        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de gamificação: {}", message, e);
        }
    }
}
