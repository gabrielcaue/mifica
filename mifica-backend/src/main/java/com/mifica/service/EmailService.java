package com.mifica.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:${MAIL_FROM:no-reply@mifica.app}}")
    private String mailFrom;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailVerificacao(String destino, String nome, String token) {
        String link = backendUrl + "/api/usuarios/verificar-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(destino);
        message.setSubject("Confirme seu e-mail - Mifica");
        message.setText(
            "Olá " + nome + ",\n\n" +
            "Clique no link abaixo para confirmar seu e-mail e ativar sua conta:\n" +
            link + "\n\n" +
            "Este link expira em 24 horas.\n\n" +
            "Se você não criou conta no Mifica, ignore este e-mail."
        );

        mailSender.send(message);
    }
}
