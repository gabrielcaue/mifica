package com.mifica.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SecureKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SecureKafkaConsumer.class);

    @KafkaListener(topics = "gamification-events", groupId = "mifica-group")
    public void consume(String message) {
        log.info("Evento de gamificação recebido: {}", message);
    }
}
