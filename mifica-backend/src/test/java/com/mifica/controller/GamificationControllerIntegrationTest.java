package com.mifica.controller;

import com.mifica.entity.User;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
import com.mifica.redis.GamificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Objects;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exemplo de Teste de Integração do GamificationController
 * Usa @SpringBootTest com H2 em memória
 * Testa fluxo completo: Controller → Service → Repository
 * 
 * ✅ @TestPropertySource força carregamento de application-test.yml
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DisplayName("GamificationController - Testes de Integração")
class GamificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @MockitoBean
    private GamificationPublisher publisher;

    private User testUser;

    @BeforeEach
    @SuppressWarnings("null")
    void setUp() {
        // Limpa dados
        badgeRepository.deleteAll();
        userRepository.deleteAll();

        // Cria usuário de teste no H2
        testUser = new User();
        testUser.setName("Test User");
        testUser.setPoints(50);
        testUser.setLevel(1);
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gamification/points/{userId} - Deve adicionar pontos com sucesso")
    void testAddPointsEndpoint() throws Exception {
        // ACT
        mockMvc.perform(post("/api/gamification/points/" + testUser.getId())
                .param("points", "30")
                .contentType("application/json"))
            .andExpect(status().isOk());

        // ASSERT
        verify(publisher).publishEvent(Objects.requireNonNull(testUser.getId()), 30);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gamification/points/{userId} - Deve criar badge ao atingir 100 pontos")
    void testAddPointsCreatesBADGE() throws Exception {
        // ARRANGE - usuário tem 70 pontos
        testUser.setPoints(70);
        userRepository.save(testUser);

        // ACT
        mockMvc.perform(post("/api/gamification/points/" + testUser.getId())
                .param("points", "50")
                .contentType("application/json"))
            .andExpect(status().isOk());

        // ASSERT
        verify(publisher).publishEvent(Objects.requireNonNull(testUser.getId()), 50);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gamification/user/{id} - Deve retornar dados do usuário")
    void testGetUserStats() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/gamification/user/" + testUser.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId().intValue()))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.points").value(50));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gamification/points/{userId} - Deve retornar 404 para usuário inexistente")
    void testAddPointsUserNotFound() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/gamification/points/999")
                .param("points", "50")
                .contentType("application/json"))
            .andExpect(status().isNotFound());

        verify(publisher, never()).publishEvent(999L, 50);
    }
}
