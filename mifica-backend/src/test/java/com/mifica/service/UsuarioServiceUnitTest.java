package com.mifica.service;

import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import com.mifica.testhelpers.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
@SuppressWarnings("null")
class UsuarioServiceUnitTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

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

}
