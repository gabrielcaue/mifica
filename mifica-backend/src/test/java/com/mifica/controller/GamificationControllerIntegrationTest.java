package com.mifica.controller;

import com.mifica.entity.User;
import com.mifica.repository.UserRepository;
import com.mifica.repository.BadgeRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    private User testUser;

    @BeforeEach
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
    @DisplayName("POST /api/gamification/add-points - Deve adicionar pontos com sucesso")
    void testAddPointsEndpoint() throws Exception {
        // ACT
        mockMvc.perform(post("/api/gamification/add-points")
                .param("userId", testUser.getId().toString())
                .param("points", "30")
                .contentType("application/json"))
            .andExpect(status().isOk());

        // ASSERT
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(80);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gamification/add-points - Deve criar badge ao atingir 100 pontos")
    void testAddPointsCreatesBADGE() throws Exception {
        // ARRANGE - usuário tem 70 pontos
        testUser.setPoints(70);
        userRepository.save(testUser);

        // ACT
        mockMvc.perform(post("/api/gamification/add-points")
                .param("userId", testUser.getId().toString())
                .param("points", "50")
                .contentType("application/json"))
            .andExpect(status().isOk());

        // ASSERT
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(120);
        assertThat(updatedUser.getLevel()).isEqualTo(2);

        // Verifica badge criada
        long badgeCount = badgeRepository.count();
        assertThat(badgeCount).isGreaterThan(0);
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
    @DisplayName("POST /api/gamification/add-points - Deve retornar 404 para usuário inexistente")
    void testAddPointsUserNotFound() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/gamification/add-points")
                .param("userId", "999")
                .param("points", "50")
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }
}
