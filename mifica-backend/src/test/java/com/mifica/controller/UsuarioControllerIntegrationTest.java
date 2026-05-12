package com.mifica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifica.dto.UsuarioDTO;
import com.mifica.dto.LoginDTO;
import com.mifica.entity.Usuario;
import com.mifica.entity.Role;
import com.mifica.repository.UsuarioRepository;
import com.mifica.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para UsuarioController
 * Usa @SpringBootTest com H2 em memória
 * Testa fluxo completo: Controller → Service → Repository
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UsuarioController - Testes de Integração")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTOValido;

    @BeforeEach
    void setUp() {
        // Limpa dados do banco H2
        usuarioRepository.deleteAll();

        // Cria um UsuarioDTO válido
        usuarioDTOValido = new UsuarioDTO();
        usuarioDTOValido.setNome("João Silva");
        usuarioDTOValido.setEmail("joao@test.com");
        usuarioDTOValido.setSenha("senha123456");
        usuarioDTOValido.setRole("USER");
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário com sucesso")
    void testCadastrarUsuario_Success() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTOValido)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("joao@test.com"))
            .andExpect(jsonPath("$.nome").value("João Silva"));

        // Verifica se foi persistido no banco
        assertThat(usuarioRepository.findByEmail("joao@test.com")).isPresent();
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com email duplicado")
    void testCadastrarUsuario_EmailDuplicado() throws Exception {
        // ARRANGE - Cria primeiro usuário
        usuarioService.criar(usuarioDTOValido);

        // ACT & ASSERT - Tenta criar com mesmo email
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTOValido)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Email já cadastrado."));
    }

    @Test
    @DisplayName("Deve normalizar email para lowercase")
    void testCadastrarUsuario_EmailNormalizado() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioComEmailMaiusculo = new UsuarioDTO();
        usuarioComEmailMaiusculo.setNome("Maria Santos");
        usuarioComEmailMaiusculo.setEmail("MARIA@TEST.COM");
        usuarioComEmailMaiusculo.setSenha("senha123456");
        usuarioComEmailMaiusculo.setRole("USER");

        // ACT
        mockMvc.perform(post("/api/usuarios/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioComEmailMaiusculo)))
            .andExpect(status().isOk());

        // ASSERT - Verifica se foi normalizado
        assertThat(usuarioRepository.findByEmail("maria@test.com")).isPresent();
        assertThat(usuarioRepository.findByEmail("MARIA@TEST.COM")).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void testBuscarUsuarioPorEmail_Success() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTOValido);

        // ACT & ASSERT
        mockMvc.perform(get("/api/usuarios/email/joao@test.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("joao@test.com"))
            .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando usuário não encontrado")
    void testBuscarUsuarioPorEmail_NaoEncontrado() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/usuarios/email/inexistente@test.com"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve validar login com credenciais corretas")
    void testValidarLogin_Sucesso() throws Exception {
        // ARRANGE
        usuarioService.criar(usuarioDTOValido);
        
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("joao@test.com");
        loginDTO.setSenha("senha123456");

        // ACT & ASSERT
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve rejeitar login com senha incorreta")
    void testValidarLogin_SenhaInvalida() throws Exception {
        // ARRANGE
        usuarioService.criar(usuarioDTOValido);
        
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("joao@test.com");
        loginDTO.setSenha("senhaErrada");

        // ACT & ASSERT
        MvcResult result = mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isUnauthorized())
            .andReturn();
    }

    @Test
    @DisplayName("Deve rejeitar login com usuário inexistente")
    void testValidarLogin_UsuarioNaoEncontrado() throws Exception {
        // ARRANGE
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("inexistente@test.com");
        loginDTO.setSenha("senha123456");

        // ACT & ASSERT
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void testListarTodosUsuarios_Success() throws Exception {
        // ARRANGE
        usuarioService.criar(usuarioDTOValido);
        
        UsuarioDTO usuario2 = new UsuarioDTO();
        usuario2.setNome("Ana Costa");
        usuario2.setEmail("ana@test.com");
        usuario2.setSenha("senha123456");
        usuario2.setRole("USER");
        usuarioService.criar(usuario2);

        // ACT & ASSERT
        mockMvc.perform(get("/api/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void testBuscarUsuarioPorId_Success() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTOValido);
        Long usuarioId = usuarioCriado.getId();

        // ACT & ASSERT
        mockMvc.perform(get("/api/usuarios/" + usuarioId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    @DisplayName("Deve atualizar perfil do usuário")
    void testAtualizarPerfil_Success() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTOValido);
        
        UsuarioDTO atualizacao = new UsuarioDTO();
        atualizacao.setNome("João Silva Atualizado");
        atualizacao.setEmail("joao@test.com");

        // ACT & ASSERT
        mockMvc.perform(put("/api/usuarios/" + usuarioCriado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizacao)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarUsuario_Success() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTOValido);
        Long usuarioId = usuarioCriado.getId();

        // ACT
        mockMvc.perform(delete("/api/usuarios/" + usuarioId))
            .andExpect(status().isOk());

        // ASSERT - Verifica se foi deletado
        assertThat(usuarioRepository.findById(usuarioId)).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar reputação do usuário")
    void testObterReputacao_Success() throws Exception {
        // ARRANGE
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTOValido);

        // ACT & ASSERT
        mockMvc.perform(get("/api/usuarios/" + usuarioCriado.getId() + "/reputacao"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reputacao").exists());
    }
}
