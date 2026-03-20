package com.mifica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mifica.entity.EmailVerificationToken;
import com.mifica.entity.Usuario;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUsuarioAndUsadoFalse(Usuario usuario);
}
