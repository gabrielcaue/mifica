package com.mifica.controller;

import com.mifica.redis.GamificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de gamificação — endpoint alternativo para adicionar pontos.
 * Publica eventos no canal Redis Pub/Sub para processamento assíncrono.
 *
 * Fluxo: POST /api/gamification/points/{userId} → Publisher → Redis → Subscriber
 */
@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    // ICP-TOTAL: 1
    // ICP-01: Controller publica evento assíncrono e não bloqueia requisição aguardando processamento.

    @Autowired
    private GamificationPublisher publisher;

    /**
     * Adiciona pontos a um usuário via Redis Pub/Sub.
     * O evento é processado de forma assíncrona pelo GamificationSubscriber.
     */
    @PostMapping("/points/{userId}")
    public String addPoints(@PathVariable Long userId, @RequestParam int points) {
        // Publica no canal Redis — subscriber processa e persiste os pontos
        publisher.publishEvent(userId, points);
        return "📤 Evento de pontos enviado via Redis para usuário " + userId;
    }
}
