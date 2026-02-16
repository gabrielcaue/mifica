package com.mifica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    private static final String SENHA_CORRETA = "AdminKafka2025!";

    @PostMapping("/conteudo")
    public ResponseEntity<String> getConteudo(@RequestBody Map<String, String> body) {
        String senha = body.get("senha");
        if (!SENHA_CORRETA.equals(senha)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Senha incorreta. Acesso negado.");
        }
        return ResponseEntity.ok("Conteúdo protegido: dados do producer/consumer...");
    }
}
