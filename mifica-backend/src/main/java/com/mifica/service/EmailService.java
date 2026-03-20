package com.mifica.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

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

        try {
            mailSender.send(message);
            log.info("📧 E-mail de confirmação enviado para {}", destino);
        } catch (MailException e) {
            log.error("❌ Falha SMTP ao enviar e-mail para {} (from={}): {}", destino, mailFrom, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao enviar e-mail para {} (from={}): {}", destino, mailFrom, e.getMessage(), e);
            throw e;
        }
    }
}
