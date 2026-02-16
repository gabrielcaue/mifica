package com.mifica.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class SecureKafkaConsumer {

    // senha especial definida em application.properties
    @Value("${admin.kafka.senha}")
    private String adminSecret;

    @KafkaListener(topics = "gamification-events", groupId = "mifica-group")
    public void consume(String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite a senha especial para visualizar a mensagem: ");
            String input = scanner.nextLine();

            if (input.equals(adminSecret)) {
                System.out.println("Mensagem recebida: " + message);
            } else {
                System.out.println("Senha incorreta. Acesso negado.");
            }
        } else {
            System.out.println("Acesso negado: apenas ADMIN pode visualizar esta mensagem.");
        }
    }
}
