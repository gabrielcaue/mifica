package com.mifica.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.mifica.redis.GamificationPublisher;
import com.mifica.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private UserRepository userRepository;

    /**
     * Adiciona pontos a um usuário via Redis Pub/Sub.
     * O evento é processado de forma assíncrona pelo GamificationSubscriber.
     */
    @PostMapping("/points/{userId}")
    public ResponseEntity<String> addPoints(@PathVariable Long userId, @RequestParam int points) {
        Long resolvedUserId = Objects.requireNonNull(userId);
        if (!userRepository.existsById(resolvedUserId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        // Publica no canal Redis — subscriber processa e persiste os pontos
        publisher.publishEvent(resolvedUserId, points);
        return ResponseEntity.ok("📤 Evento de pontos enviado via Redis para usuário " + resolvedUserId);
    }

    /**
     * Retorna os dados de gamificação do usuário.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserStats(@PathVariable Long id) {
        Long resolvedId = Objects.requireNonNull(id);
        return userRepository.findById(resolvedId)
            .<ResponseEntity<?>>map(user -> {
                Map<String, Object> resposta = new HashMap<>();
                resposta.put("id", user.getId());
                resposta.put("name", user.getName());
                resposta.put("points", user.getPoints());
                resposta.put("level", user.getLevel());
                return ResponseEntity.ok(resposta);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."));
    }
}
