package com.mifica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GamificationEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishEvent(Long userId, int points) {
        String message = "User:" + userId + " Points:" + points;
        kafkaTemplate.send("gamification-events", message);
        System.out.println("📤 Evento enviado: " + message);
    }
}
