package com.mifica.controller;

import com.mifica.redis.GamificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    @Autowired
    private GamificationPublisher publisher;

    @PostMapping("/points/{userId}")
    public String addPoints(@PathVariable Long userId, @RequestParam int points) {
        publisher.publishEvent(userId, points);
        return "📤 Evento de pontos enviado via Redis para usuário " + userId;
    }
}
