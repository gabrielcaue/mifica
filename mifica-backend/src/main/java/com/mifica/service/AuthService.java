package com.mifica.service;

import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // ICP-TOTAL: 1
    // Classe simples: Apenas consulta repositório e aplica filtro de validação.

    @Autowired
    private UsuarioRepository usuarioRepo;

    public Usuario autenticar(String email, String senha) {
        return usuarioRepo.findByEmail(email)
                .filter(u -> u.getSenha().equals(senha))
                .orElse(null);
    }
}
