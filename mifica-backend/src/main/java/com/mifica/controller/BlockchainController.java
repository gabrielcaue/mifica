package com.mifica.controller;

import com.mifica.dto.TransacaoBlockchainDTO;
import com.mifica.blockchain.BlockchainService;
import com.mifica.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    // ICP-TOTAL: 1
    // ICP-01: Controller atua como fachada REST para operações de registro e listagem blockchain.

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/transacoes")
    public ResponseEntity<?> registrar(@RequestHeader("Authorization") String token,
                                       @RequestBody TransacaoBlockchainDTO dto) {
        try {
            String jwt = token.replace("Bearer ", "");
            String emailRemetente = jwtUtil.extrairEmail(jwt);
            String roleRemetente = jwtUtil.extrairRole(jwt);

            TransacaoBlockchainDTO registrada = blockchainService.registrarTransacao(emailRemetente, roleRemetente, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro ao registrar transação.");
        }
    }

    @GetMapping("/transacoes")
    public ResponseEntity<List<TransacaoBlockchainDTO>> listar() {
        return ResponseEntity.ok(blockchainService.listarTransacoes());
    }
}
