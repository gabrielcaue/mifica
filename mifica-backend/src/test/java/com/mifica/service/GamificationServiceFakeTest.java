package com.mifica.service;

import com.mifica.entity.User;
import com.mifica.entity.Badge;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
import com.mifica.repository.FakeUserRepository;
import com.mifica.repository.FakeBadgeRepository;
import com.mifica.testhelpers.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exemplo de Teste de Integração com FAKE Repository
 * Testa lógica de negócio com persistência em memória
 */
@DisplayName("GamificationService - Testes com Fake Repository")
class GamificationServiceFakeTest {

    private GamificationService gamificationService;
    private FakeUserRepository fakeUserRepository;
    private FakeBadgeRepository fakeBadgeRepository;

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        fakeBadgeRepository = new FakeBadgeRepository();
        gamificationService = new GamificationService(fakeUserRepository, fakeBadgeRepository);
    }

    @Test
    @DisplayName("Deve persistir pontos e badge corretamente no fluxo completo")
    void testCompleteGamificationFlow() {
        // ARRANGE - Cria usuário no fake repository
        User user = TestDataFactory.userBuilder()
            .withName("Alice")
            .withPoints(0)
            .withLevel(1)
            .build();
        
        user = fakeUserRepository.save(user);

        // ACT - Adiciona pontos que atingem o limiar
        gamificationService.addPoints(user.getId(), 100);

        // ASSERT - Verifica persistência
        User updatedUser = fakeUserRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(100);
        assertThat(updatedUser.getLevel()).isEqualTo(2);

        // Verifica que badge foi criada
        assertThat(fakeBadgeRepository.findByUserId(user.getId())).isNotEmpty();
        Badge badge = fakeBadgeRepository.findByUserId(user.getId()).get(0);
        assertThat(badge.getName()).contains("Level 2");
    }

    @Test
    @DisplayName("Deve acumular múltiplas adições de pontos")
    void testMultiplePointAdditions() {
        // ARRANGE
        User user = fakeUserRepository.save(
            TestDataFactory.userBuilder().withPoints(0).build()
        );

        // ACT
        gamificationService.addPoints(user.getId(), 30);
        gamificationService.addPoints(user.getId(), 40);
        gamificationService.addPoints(user.getId(), 40); // Total: 110 → Level 2

        // ASSERT
        User finalUser = fakeUserRepository.findById(user.getId()).orElseThrow();
        assertThat(finalUser.getPoints()).isEqualTo(110);
        assertThat(finalUser.getLevel()).isEqualTo(2);
        assertThat(fakeBadgeRepository.count()).isEqualTo(1);
    }
}
