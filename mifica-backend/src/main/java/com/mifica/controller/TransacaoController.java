package com.mifica.controller;

import com.mifica.entity.Transacao;
import com.mifica.service.TransacaoService;
import com.mifica.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    // ICP-TOTAL: 2
    // ICP-01: Controller depende de extração de identidade por token para escopo de dados financeiros.

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTransacoes(@RequestHeader("Authorization") String token) {
        // ICP-02: Tratamento de autenticação e fallback de autorização são feitos na borda HTTP.
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            List<Transacao> transacoes = transacaoService.listarPorEmail(email);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> criarTransacao(@RequestHeader("Authorization") String token,
                                                 @RequestBody Transacao transacao) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            transacaoService.criarTransacao(email, transacao);
            return ResponseEntity.ok("Transação criada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Erro ao criar transação.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletarTransacao(@PathVariable Long id) {
        transacaoService.deletarPorId(id);
        return ResponseEntity.ok("Transação deletada.");
    }
}
