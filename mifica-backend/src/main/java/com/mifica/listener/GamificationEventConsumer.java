package com.mifica.listener;

import com.mifica.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GamificationEventConsumer {

    @Autowired
    private GamificationService gamificationService;

    @KafkaListener(topics = "gamification-events", groupId = "mifica-group")
    public void listen(String message) {
        System.out.println("📩 Evento recebido: " + message);

        // Exemplo de mensagem: "User:1 Points:50"
        String[] parts = message.split(" ");
        Long userId = Long.parseLong(parts[0].split(":")[1]);
        int points = Integer.parseInt(parts[1].split(":")[1]);

        gamificationService.addPoints(userId, points);
    }
}
