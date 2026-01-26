package com.mifica.controller;

import com.mifica.service.GamificationEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    @Autowired
    private GamificationEventProducer producer;

    @PostMapping("/points/{userId}")
    public String addPoints(@PathVariable Long userId, @RequestParam int points) {
        // dispara evento no Kafka através do Producer
        producer.publishEvent(userId, points);
        return "📤 Evento de pontos enviado para usuário " + userId;
    }
}
