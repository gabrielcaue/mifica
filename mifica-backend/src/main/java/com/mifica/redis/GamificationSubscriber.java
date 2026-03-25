package com.mifica.redis;

import com.mifica.service.GamificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Subscriber Redis Pub/Sub para eventos de gamificação.
 * Processa mensagens do canal "gamification-events" e delega ao GamificationService.
 *
 * As últimas mensagens recebidas ficam armazenadas em memória e só podem ser
 * visualizadas via SecureController mediante a senha correta (admin.redis.senha).
 */
@Component
public class GamificationSubscriber {

    // ICP-TOTAL: 3
    // ICP-01: Subscriber combina observabilidade, buffer em memória e parsing de protocolo textual de eventos.

    private static final Logger log = LoggerFactory.getLogger(GamificationSubscriber.class);
    private static final int MAX_MENSAGENS = 50;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final GamificationService gamificationService;
    private final ConcurrentLinkedDeque<String> mensagensRecebidas = new ConcurrentLinkedDeque<>();

    public GamificationSubscriber(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    /**
     * Chamado automaticamente pelo RedisMessageListenerContainer quando uma mensagem
     * é publicada no canal "gamification-events".
     * Formato esperado: "User:{userId} Points:{points}"
     */
    public void onMessage(String message) {
        // ICP-02: Fluxo mistura retenção de histórico, parsing defensivo e delegação transacional de pontos.
        log.info("📥 Evento de gamificação recebido: {}", message);

        // Armazena a mensagem com timestamp no buffer circular (máx. 50)
        // Essas mensagens só podem ser visualizadas via SecureController com senha
        String registro = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        mensagensRecebidas.addFirst(registro);
        while (mensagensRecebidas.size() > MAX_MENSAGENS) {
            mensagensRecebidas.removeLast();
        }

        try {
            // Faz o parse da mensagem no formato "User:{id} Points:{pontos}"
            String[] parts = message.split(" ");
            Long userId = Long.parseLong(parts[0].split(":")[1]);
            int points = Integer.parseInt(parts[1].split(":")[1]);

            // Delega ao GamificationService para persistir pontos e verificar badges
            gamificationService.addPoints(userId, points);
            log.info("✅ Pontos processados: userId={}, points={}", userId, points);
        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de gamificação: {}", message, e);
        }
    }

    /**
     * Retorna as últimas mensagens recebidas (máx. 50).
     * Somente deve ser chamado após validação de senha (via SecureController).
     */
    public List<String> getMensagens() {
        // ICP-03: Retorno defensivo evita exposição da estrutura concorrente interna.
        return new ArrayList<>(mensagensRecebidas);
    }

    /**
     * Retorna quantas mensagens estão armazenadas.
     */
    public int getTotal() {
        return mensagensRecebidas.size();
    }
}
