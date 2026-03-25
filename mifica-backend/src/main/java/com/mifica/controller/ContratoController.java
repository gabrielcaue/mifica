package com.mifica.controller;

import com.mifica.dto.ContratoDTO;
import com.mifica.service.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    // ICP-TOTAL: 1
    // ICP-01: Controller simplifica fluxo de criação/listagem preservando semântica HTTP adequada.

    @Autowired
    private ContratoService contratoService;

    @PostMapping
    public ResponseEntity<ContratoDTO> criar(@RequestBody ContratoDTO dto) {
        ContratoDTO criado = contratoService.criarContrato(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<ContratoDTO>> listar() {
        return ResponseEntity.ok(contratoService.listarContratos());
    }
}
