package com.mifica.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @Value("${admin.kafka.senha}")
    private String senhaCorreta;

    @PostMapping("/conteudo")
    public ResponseEntity<String> getConteudo(@RequestBody Map<String, String> body) {
        String senha = body.get("senha");
        if (!senhaCorreta.equals(senha)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Senha incorreta. Acesso negado.");
        }
        return ResponseEntity.ok("Conteúdo protegido: dados do producer/consumer...");
    }
}
