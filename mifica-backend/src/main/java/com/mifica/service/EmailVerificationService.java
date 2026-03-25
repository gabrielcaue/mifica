package com.mifica.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mifica.entity.EmailVerificationToken;
import com.mifica.entity.Usuario;
import com.mifica.repository.EmailVerificationTokenRepository;
import com.mifica.repository.UsuarioRepository;

@Service
public class EmailVerificationService {

    // ICP-TOTAL: 2
    // ICP-01: Serviço controla ciclo de vida de token com política de expiração e uso único.

    private final EmailVerificationTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    UsuarioRepository usuarioRepository) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public String gerarToken(Usuario usuario) {
        tokenRepository.deleteByUsuarioAndUsadoFalse(usuario);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiraEm(LocalDateTime.now().plusHours(24));
        token.setUsado(false);

        return tokenRepository.save(token).getToken();
    }

    @Transactional
    public String verificarToken(String tokenRecebido) {
        // ICP-02: Verificação executa sequência de validações com efeitos colaterais em usuário e token.
        EmailVerificationToken token = tokenRepository.findByToken(tokenRecebido).orElse(null);

        if (token == null) {
            return "Token de verificação inválido.";
        }

        if (token.isUsado()) {
            return "Este link já foi utilizado.";
        }

        if (token.getExpiraEm().isBefore(LocalDateTime.now())) {
            return "Link expirado. Solicite um novo e-mail de confirmação.";
        }

        Usuario usuario = token.getUsuario();
        usuario.setEnabled(Boolean.TRUE);
        usuario.setEmailVerificado(Boolean.TRUE);
        usuarioRepository.save(usuario);

        token.setUsado(true);
        tokenRepository.save(token);

        return null;
    }
}
