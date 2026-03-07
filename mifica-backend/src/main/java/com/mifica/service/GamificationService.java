package com.mifica.service;

import com.mifica.entity.User;
import com.mifica.entity.Badge;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
import org.springframework.stereotype.Service;

@Service
public class GamificationService {
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public GamificationService(UserRepository userRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    public void addPoints(Long userId, int points) {
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId)).orElseThrow();
        user.setPoints(user.getPoints() + points);

        // Exemplo de regra: ao atingir 100 pontos, sobe de nível e ganha badge
        if (user.getPoints() >= 100 && user.getLevel() < 2) {
            user.setLevel(2);
            Badge badge = new Badge();
            badge.setName("Level 2 Unlocked");
            badge.setDescription("Parabéns, você atingiu 100 pontos!");
            badge.setUserId(user.getId());
            badgeRepository.save(badge);
            System.out.println("🏅 Badge concedido ao usuário " + user.getName());
        }

        userRepository.save(user);
        System.out.println("✅ Pontos atualizados para usuário " + user.getName());
    }
}
