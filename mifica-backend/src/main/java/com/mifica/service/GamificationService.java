package com.mifica.service;

import com.mifica.entity.User;
import com.mifica.entity.Badge;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
import org.springframework.stereotype.Service;

/**
 * Serviço de gamificação — processa pontos e concede badges.
 * Chamado pelo GamificationSubscriber quando uma mensagem chega do Redis Pub/Sub.
 *
 * Regras:
 * - Soma pontos ao usuário
 * - Se atingir 100 pontos, sobe para nível 2 e ganha badge
 */
@Service
public class GamificationService {

    // ICP-TOTAL: 2
    // ICP-01: Regra de pontuação possui gatilho de badge e evolução de nível baseada em limiar.
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public GamificationService(UserRepository userRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    /**
     * Adiciona pontos ao usuário e verifica se deve conceder badge.
     * Persiste a atualização no banco de dados (MySQL via JPA).
     */
    public void addPoints(Long userId, int points) {
        // ICP-02: Atualização combina consistência de saldo de pontos com concessão condicional de badge.
        // Busca usuário no banco — lança exceção se não existir
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId)).orElseThrow();
        // Soma os novos pontos ao total existente
        user.setPoints(user.getPoints() + points);

        // Verifica se o usuário atingiu 100 pontos para subir de nível
        if (user.getPoints() >= 100 && user.getLevel() < 2) {
            user.setLevel(2);
            // Cria e persiste badge de conquista
            Badge badge = new Badge();
            badge.setName("Level 2 Unlocked");
            badge.setDescription("Parabéns, você atingiu 100 pontos!");
            badge.setUserId(user.getId());
            badgeRepository.save(badge);
        }

        // Persiste a atualização de pontos no banco
        userRepository.save(user);
    }
}
