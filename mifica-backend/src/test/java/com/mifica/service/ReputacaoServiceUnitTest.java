package com.mifica.service;

import com.mifica.dto.HistoricoReputacaoDTO;
import com.mifica.entity.HistoricoReputacao;
import com.mifica.entity.Usuario;
import com.mifica.repository.HistoricoReputacaoRepository;
import com.mifica.repository.UsuarioRepository;
import com.mifica.testhelpers.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários para ReputacaoService
 * Usa MOCKS para isolar a lógica de negócio de reputação
 */
@DisplayName("ReputacaoService - Testes Unitários")
@SuppressWarnings("null")
class ReputacaoServiceUnitTest {

    @InjectMocks
    private ReputacaoService reputacaoService;

    @Mock
    private HistoricoReputacaoRepository historicoReputacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve registrar alteração de reputação com sucesso")
    void testRegistrarAlteracao_Success() {
        // ARRANGE
        String email = "usuario@test.com";
        int novaReputacao = 150;
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withEmail(email)
            .withReputacao(100)
            .build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(historicoReputacaoRepository.save(any(HistoricoReputacao.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioService.atualizarReputacao(email, novaReputacao)).thenReturn(true);

        // ACT
        reputacaoService.registrarAlteracao(email, novaReputacao);

        // ASSERT
        verify(historicoReputacaoRepository, times(1)).save(argThat((HistoricoReputacao historico) -> historico != null));
        verify(usuarioService, times(1)).atualizarReputacao(email, novaReputacao);
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Não deve registrar alteração quando usuário não existe")
    void testRegistrarAlteracao_UsuarioNaoEncontrado() {
        // ARRANGE
        String email = "inexistente@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // ACT
        reputacaoService.registrarAlteracao(email, 100);

        // ASSERT
        verify(historicoReputacaoRepository, never()).save(any(HistoricoReputacao.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve registrar histórico com reputação anterior correta")
    void testRegistrarAlteracao_HistoricoComReputacaoAnterior() {
        // ARRANGE
        String email = "usuario@test.com";
        int reputacaoAnterior = 50;
        int novaReputacao = 120;
        
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withEmail(email)
            .withReputacao(reputacaoAnterior)
            .build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(historicoReputacaoRepository.save(any(HistoricoReputacao.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioService.atualizarReputacao(email, 100)).thenReturn(true);

        // ACT
        reputacaoService.registrarAlteracao(email, novaReputacao);

        // ASSERT
        verify(historicoReputacaoRepository).save(argThat((HistoricoReputacao historico) ->
            historico.getReputacaoAnterior() == reputacaoAnterior && 
            historico.getReputacaoNova() == novaReputacao
        ));
        verify(usuarioService).atualizarReputacao(email, novaReputacao);
    }

    @Test
    @DisplayName("Deve listar histórico de reputação do usuário")
    void testListarHistorico_Success() {
        // ARRANGE
        String email = "usuario@test.com";
        HistoricoReputacao historico1 = new HistoricoReputacao();
        historico1.setId(1L);
        historico1.setEmailUsuario(email);
        historico1.setReputacaoAnterior(50);
        historico1.setReputacaoNova(100);
        historico1.setDataAlteracao(LocalDateTime.now());

        HistoricoReputacao historico2 = new HistoricoReputacao();
        historico2.setId(2L);
        historico2.setEmailUsuario(email);
        historico2.setReputacaoAnterior(100);
        historico2.setReputacaoNova(150);
        historico2.setDataAlteracao(LocalDateTime.now());

        when(historicoReputacaoRepository.findByEmailUsuario(email))
            .thenReturn(List.of(historico1, historico2));

        // ACT
        List<HistoricoReputacaoDTO> resultado = reputacaoService.listarHistorico(email);

        // ASSERT
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getReputacaoAnterior()).isEqualTo(50);
        assertThat(resultado.get(1).getReputacaoNova()).isEqualTo(150);
        verify(historicoReputacaoRepository, times(1)).findByEmailUsuario(email);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há histórico")
    void testListarHistorico_Vazio() {
        // ARRANGE
        String email = "usuario@test.com";
        when(historicoReputacaoRepository.findByEmailUsuario(email)).thenReturn(List.of());

        // ACT
        List<HistoricoReputacaoDTO> resultado = reputacaoService.listarHistorico(email);

        // ASSERT
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve aumentar reputação progressivamente")
    void testRegistrarAlteracao_AumentoProgressivo() {
        // ARRANGE
        String email = "usuario@test.com";
        Usuario usuario = TestDataFactory.usuarioBuilder()
            .withEmail(email)
            .withReputacao(0)
            .build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(historicoReputacaoRepository.save(any(HistoricoReputacao.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioService.atualizarReputacao(email, 100)).thenReturn(true);
        when(usuarioService.atualizarReputacao(email, 200)).thenReturn(true);
        when(usuarioService.atualizarReputacao(email, 300)).thenReturn(true);

        // ACT
        reputacaoService.registrarAlteracao(email, 100);
        reputacaoService.registrarAlteracao(email, 200);
        reputacaoService.registrarAlteracao(email, 300);

        // ASSERT
        verify(historicoReputacaoRepository, times(3)).save(any(HistoricoReputacao.class));
        verify(usuarioService, times(1)).atualizarReputacao(email, 100);
        verify(usuarioService, times(1)).atualizarReputacao(email, 200);
        verify(usuarioService, times(1)).atualizarReputacao(email, 300);
    }
}
