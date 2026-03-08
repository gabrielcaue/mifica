package com.mifica.controller;

import com.mifica.redis.GamificationSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de acesso protegido por senha.
 * Permite visualizar as mensagens recebidas pelo subscriber Redis Pub/Sub
 * somente mediante a senha correta (admin.redis.senha).
 *
 * Endpoint: POST /api/secure/conteudo
 * Body: { "senha": "..." }
 * Retorno: { total, canal, mensagens[] }
 */
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @Value("${admin.redis.senha}")
    private String senhaCorreta;

    @Autowired
    private GamificationSubscriber subscriber;

    /**
     * Endpoint protegido por senha.
     * Somente com a senha correta é possível visualizar as mensagens
     * recebidas pelo subscriber Redis Pub/Sub.
     */
    @PostMapping("/conteudo")
    public ResponseEntity<?> getConteudo(@RequestBody Map<String, String> body) {
        String senha = body.get("senha");
        if (!senhaCorreta.equals(senha)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("erro", "Senha incorreta. Acesso negado."));
        }

        List<String> mensagens = subscriber.getMensagens();

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("total", subscriber.getTotal());
        resposta.put("canal", "gamification-events");
        resposta.put("mensagens", mensagens);

        return ResponseEntity.ok(resposta);
    }
}
