package com.mifica.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifica.entity.Usuario;
import com.mifica.util.JwtService;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Autentica o usuário e retorna um mapa com token + informações mínimas do usuário.
     */
    public Map<String, Object> authenticate(String email, String senha) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas.");
        }

        String token = jwtService.gerarToken(usuario);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());
        resposta.put("role", usuario.getRole());
        resposta.put("reputacao", usuario.getReputacao());
        resposta.put("conquistas", usuario.getConquistas());

        return resposta;
    }
}
