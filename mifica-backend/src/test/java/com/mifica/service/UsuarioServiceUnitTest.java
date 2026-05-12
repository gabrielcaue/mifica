package com.mifica.service;

import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import com.mifica.testhelpers.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Exemplo de Testes Unitários para UsuarioService
 * 
 * Testes focados em:
 * - Validação de dados
 * - Criptografia de senha
 * - Regras de negócio
 * - Detecção de erros
 */
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceUnitTest {

    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService();
        usuarioService.usuarioRepository = usuarioRepository;
        usuarioService.passwordEncoder = passwordEncoder;
    }

    // ============ TESTES DE CRIAÇÃO ============

    @Test
    @DisplayName("Deve criar usuário com senha criptografada")
    void testCriarUsuario_Success() {
        // ARRANGE
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@example.com");
        dto.setSenha("senha123");
        dto.setReputacao(1);
        dto.setRole("USER");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(dto.getNome());
        usuarioSalvo.setEmail(dto.getEmail());
        usuarioSalvo.setSenha("$2a$10$encrypted..."); // Hash BCrypt
        usuarioSalvo.setReputacao(1);

        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encrypted...");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        // ACT
        UsuarioDTO resultado = usuarioService.criar(dto);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        
        // Verifica que save foi chamado
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        
        // Verifica que senha foi criptografada
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    @DisplayName("Deve verificar se email já existe")
    void testEmailJaExiste_True() {
        // ARRANGE
        when(usuarioRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(new Usuario()));

        // ACT
        boolean existe = usuarioService.emailJaExiste("joao@example.com");

        // ASSERT
        assertThat(existe).isTrue();
        verify(usuarioRepository).findByEmail("joao@example.com");
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void testEmailJaExiste_False() {
        // ARRANGE
        when(usuarioRepository.findByEmail("novo@example.com"))
            .thenReturn(Optional.empty());

        // ACT
        boolean existe = usuarioService.emailJaExiste("novo@example.com");

        // ASSERT
        assertThat(existe).isFalse();
    }

    // ============ TESTES DE AUTENTICAÇÃO ============

    @Test
    @DisplayName("Deve validar login com credenciais corretas")
    void testValidarLogin_Success() {
        // ARRANGE
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withEmail("joao@example.com")
            .build();

        when(usuarioRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(usuario));
        
        when(passwordEncoder.matches("senha123", usuario.getSenha()))
            .thenReturn(true);

        // ACT
        boolean valido = usuarioService.validarLogin("joao@example.com", "senha123");

        // ASSERT
        assertThat(valido).isTrue();
        verify(passwordEncoder).matches("senha123", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve rejeitar login com senha inválida")
    void testValidarLogin_SenhaInvalida() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@example.com");
        usuario.setSenha("$2a$10$hash...");

        when(usuarioRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(usuario));
        
        when(passwordEncoder.matches("senhaErrada", usuario.getSenha()))
            .thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> 
            usuarioService.validarLogin("joao@example.com", "senhaErrada")
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Senha inválida");

        verify(passwordEncoder).matches("senhaErrada", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não encontrado no login")
    void testValidarLogin_UsuarioNaoEncontrado() {
        // ARRANGE
        when(usuarioRepository.findByEmail("inexistente@example.com"))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() ->
            usuarioService.validarLogin("inexistente@example.com", "senha123")
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Usuário não encontrado");

        verify(passwordEncoder, never()).matches(any(), any());
    }

    // ============ TESTES DE REPUTAÇÃO ============

    @Test
    @DisplayName("Deve incrementar reputação corretamente")
    void testIncrementarReputacao() {
        // ARRANGE
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withReputacao(5)
            .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // ACT
        usuarioService.incrementarReputacao(1L, 3);

        // ASSERT
        assertThat(usuario.getReputacao()).isEqualTo(8);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve respeitar limite máximo de reputação")
    void testIncrementarReputacao_LimiteMaximo() {
        // ARRANGE
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withReputacao(95) // Próximo do máximo
            .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // ACT
        usuarioService.incrementarReputacao(1L, 10);

        // ASSERT
        assertThat(usuario.getReputacao()).isLessThanOrEqualTo(100);
    }

    // ============ TESTES DE MUDANÇA DE SENHA ============

    @Test
    @DisplayName("Deve alterar senha com validação correta")
    void testMudarSenha_Success() {
        // ARRANGE
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withEmail("joao@example.com")
            .build();

        when(usuarioRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(usuario));
        
        when(passwordEncoder.matches("senhaAntiga", usuario.getSenha()))
            .thenReturn(true);
        
        when(passwordEncoder.encode("novaSenha"))
            .thenReturn("$2a$10$novaSenhaEncriptada...");
        
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // ACT
        usuarioService.mudarSenha("joao@example.com", "senhaAntiga", "novaSenha");

        // ASSERT
        verify(passwordEncoder).matches("senhaAntiga", usuario.getSenha());
        verify(passwordEncoder).encode("novaSenha");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve rejeitar mudança de senha se antiga está errada")
    void testMudarSenha_SenhaAntigaErrada() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@example.com");
        usuario.setSenha("$2a$10$hash...");

        when(usuarioRepository.findByEmail("joao@example.com"))
            .thenReturn(Optional.of(usuario));
        
        when(passwordEncoder.matches("senhaErrada", usuario.getSenha()))
            .thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() ->
            usuarioService.mudarSenha("joao@example.com", "senhaErrada", "novaSenha")
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Senha antiga inválida");

        // Não deve tentar salvar
        verify(usuarioRepository, never()).save(any());
    }

    // ============ TESTES DE VALIDAÇÃO ============

    @Test
    @DisplayName("Deve validar formato de email")
    void testValidarEmail_Invalido() {
        // ACT & ASSERT
        assertThatThrownBy(() ->
            usuarioService.validarEmail("emailInvalido")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve validar comprimento mínimo da senha")
    void testValidarSenha_MuitoCurta() {
        // ACT & ASSERT
        assertThatThrownBy(() ->
            usuarioService.validarSenha("123")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Senha deve ter no mínimo 8 caracteres");
    }
}
