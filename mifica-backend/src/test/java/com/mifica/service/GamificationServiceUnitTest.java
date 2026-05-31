package com.mifica.service;

import com.mifica.entity.User;
import com.mifica.entity.Badge;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
import com.mifica.testhelpers.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Exemplo de Teste Unitário do GamificationService
 * Usa MOCKS para isolar a lógica de negócio
 */
@DisplayName("GamificationService - Testes Unitários")
@SuppressWarnings("null")
class GamificationServiceUnitTest {

    private GamificationService gamificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gamificationService = new GamificationService(userRepository, badgeRepository);
    }

    @Test
    @DisplayName("Deve adicionar pontos ao usuário sem atingir nível 2")
    void testAddPoints_NoLevelUp() {
        // ARRANGE
        User user = TestDataFactory.userBuilder()
            .withId(1L)
            .withPoints(50)
            .withLevel(1)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // ACT
        gamificationService.addPoints(1L, 30);

        // ASSERT
        assertThat(user.getPoints()).isEqualTo(80);
        assertThat(user.getLevel()).isEqualTo(1);
        
        // Verifica que NÃO criou badge
        verify(badgeRepository, never()).save(any(Badge.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve subir para nível 2 e criar badge ao atingir 100 pontos")
    void testAddPoints_ReachesLevel2AndCreatesBadge() {
        // ARRANGE
        User user = TestDataFactory.userBuilder()
            .withId(1L)
            .withPoints(70)
            .withLevel(1)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // ACT
        gamificationService.addPoints(1L, 50);

        // ASSERT
        assertThat(user.getPoints()).isEqualTo(120);
        assertThat(user.getLevel()).isEqualTo(2);

        // Verifica que criou badge
        verify(badgeRepository, times(1)).save(any(Badge.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existe")
    void testAddPoints_UserNotFound() {
        // ARRANGE
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        try {
            gamificationService.addPoints(999L, 50);
            throw new AssertionError("Deveria ter lançado exceção");
        } catch (java.util.NoSuchElementException e) {
            // Expected
        }

        verify(badgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve respeitar limite de pontos já em nível 2")
    void testAddPoints_AlreadyAtMaxLevel() {
        // ARRANGE
        User user = TestDataFactory.userBuilder()
            .withId(1L)
            .withPoints(150)
            .withLevel(2)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // ACT
        gamificationService.addPoints(1L, 50);

        // ASSERT
        assertThat(user.getPoints()).isEqualTo(200);
        assertThat(user.getLevel()).isEqualTo(2); // Não sobe mais

        // Não cria badge se já está no nível máximo
        verify(badgeRepository, never()).save(any(Badge.class));
    }
}
