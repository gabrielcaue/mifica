package com.mifica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // ICP-TOTAL: 0
    // Classe trivial: Apenas retorna mensagem estática para health check.

    @GetMapping("/")
    public String home() {
        return "API Mifica rodando 🚀";
    }
}
