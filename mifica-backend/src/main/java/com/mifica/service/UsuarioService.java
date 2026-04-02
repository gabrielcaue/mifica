package com.mifica.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Role;
import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;

/**
 * Serviço principal de usuários — gerencia CRUD, autenticação,
 * reputação, conquistas, recompensas e alteração de senha.
 *
 * Senhas são criptografadas com BCrypt antes de persistir.
 * Todas as operações de banco são feitas via Spring Data JPA (UsuarioRepository).
 */
@Service
public class UsuarioService {

    // ICP-TOTAL: 7
    // ICP-01: Serviço central concentra regras de autenticação, reputação, recompensas e manutenção de perfil.

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean emailJaExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Cadastro com senha criptografada
    public UsuarioDTO criar(UsuarioDTO dto) {
        // ICP-02: Cadastro exige criptografia antes da persistência para manter invariantes de segurança.
        String senhaCriptografada = criptografarSenha(dto.getSenha());
        return criarUsuario(dto, senhaCriptografada);
    }

    private UsuarioDTO criarUsuario(UsuarioDTO dto, String senhaCriptografada) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(senhaCriptografada); // ✅ corrigido
        usuario.setEnabled(Boolean.TRUE);
        usuario.setReputacao(dto.getReputacao() != null ? dto.getReputacao() : 1);
        usuario.setNivel(dto.getNivel());
        usuario.setRole(formatarPapel(dto.getRole()));

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);
    }

    // ✅ Novo método para login
    /**
     * Valida credenciais de login.
     * Compara senha digitada com hash BCrypt armazenado no banco.
     */
    public boolean validarLogin(String email, String senhaDigitada) {
        // ICP-03: Autenticação combina múltiplas guard clauses (existência, ativação e senha).
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado");
        if (!passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }
        return true;
    }

    private Role formatarPapel(String role) {
        if (role == null || role.isBlank()) {
            return Role.ROLE_USER;
        }
        if (role.toUpperCase().startsWith("ROLE_")) {
            return Role.valueOf(role.toUpperCase());
        }
        return Role.valueOf("ROLE_" + role.toUpperCase());
    }

    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(Objects.requireNonNull(id)).map(this::converterParaDTO);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public UsuarioDTO buscarPorEmailDTO(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? converterParaDTO(usuario) : null;
    }

    public void atualizarPerfil(String email, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        if (dadosAtualizados.getNome() != null) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getSenha() != null) {
            usuario.setSenha(criptografarSenha(dadosAtualizados.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    public boolean atualizarReputacao(String email, int novaReputacao) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return false;

        usuario.setReputacao(novaReputacao);
        usuarioRepository.save(usuario);
        return true;
    }

    public void atualizarReputacao(Usuario usuario, int novaReputacao) {
        usuario.setReputacao(novaReputacao);
        usuario.atualizarNivel();
        usuarioRepository.save(usuario);
    }

    public Usuario criarSolicitacao(BigDecimal valor, String descricao, LocalDate prazo, String email) {
        Usuario usuario = buscarPorEmail(email);

        SolicitacaoCredito nova = new SolicitacaoCredito();
        nova.setValorSolicitado(valor);
        nova.setDescricao(descricao);
        nova.setPrazoPagamento(prazo);
        nova.setStatus("PENDENTE");
        nova.setDataSolicitacao(LocalDateTime.now());

        usuario.adicionarSolicitacao(nova);
        return usuarioRepository.save(usuario);
    }

    public List<SolicitacaoCredito> listarSolicitacoes(String email) {
        Usuario usuario = buscarPorEmail(email);
        return usuario != null ? usuario.getSolicitacoes() : List.of();
    }

    public boolean verificarMissaoDiaria(Usuario usuario) {
        return usuario.cumpriuMissaoHoje();
    }

    public List<String> listarConquistas(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado.");
        return usuario.getConquistas();
    }

    public void aplicarRecompensas(String email) {
        // ICP-04: Aplicação de recompensas depende de estado incremental de conquistas e reputação.
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return;

        if (usuario.getConquistas() == null) {
            usuario.setConquistas(new ArrayList<>());
        }

        if (!usuario.getConquistas().contains("Primeira solicitação") && usuario.getSolicitacoes().size() >= 1) {
            usuario.getConquistas().add("Primeira solicitação");
        }

        if (usuario.getReputacao() >= 5 && !usuario.getConquistas().contains("Reputação 5+")) {
            usuario.getConquistas().add("Reputação 5+");
        }

        usuarioRepository.save(usuario);
    }

    public void aplicarRecompensasCertas(Usuario usuario) {
        // ICP-05: Regras de progressão usam combinação de missão diária, marcos de reputação e ajuste de nível.
        boolean cumpriuMissao = verificarMissaoDiaria(usuario);
        if (cumpriuMissao) {
            usuario.setReputacao(usuario.getReputacao() + 1);
        }

        if (usuario.getConquistas() == null) {
            usuario.setConquistas(new ArrayList<>());
        }

        if (usuario.getSolicitacoes().size() == 1 && !usuario.getConquistas().contains("Primeira solicitação")) {
            usuario.getConquistas().add("Primeira solicitação");
        }

        if (usuario.getReputacao() >= 10 && !usuario.getConquistas().contains("Reputação 10+")) {
            usuario.getConquistas().add("Reputação 10+");
        }

        if (usuario.getReputacao() >= 20) {
            usuario.setNivel("Expert");
        } else if (usuario.getReputacao() >= 10) {
            usuario.setNivel("Intermediário");
        } else {
            usuario.setNivel("Iniciante");
        }

        usuarioRepository.save(usuario);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(Objects.requireNonNull(usuario));
    }

    public void excluir(Usuario usuario) {
        usuarioRepository.delete(Objects.requireNonNull(usuario));
    }

    public void deletarPorEmail(String email) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) throw new RuntimeException("Usuário não encontrado para exclusão.");
        usuarioRepository.delete(usuario);
    }

    public boolean senhaCorreta(String senhaDigitada, String senhaArmazenada) {
        return passwordEncoder.matches(senhaDigitada, senhaArmazenada);
    }

    private String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    // ✅ Agora é público para o controller usar
    public UsuarioDTO converterParaDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            null, // nunca expor senha no DTO
            usuario.getReputacao(),
            usuario.getNivel(),
            usuario.getDataNascimento(),
            usuario.getTelefone(),
            usuario.getRole() != null ? usuario.getRole().name() : null
        );
    }

    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(this::converterParaDTO).toList();
    }

    public Optional<UsuarioDTO> atualizar(Long id, UsuarioDTO dto) {
        // ICP-06: Atualização parcial mantém campos opcionais e aplica transformação de papel/senha quando necessário.
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(Objects.requireNonNull(id));
        if (optionalUsuario.isEmpty()) return Optional.empty();

        Usuario usuario = optionalUsuario.get();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(criptografarSenha(dto.getSenha()));
        }

        if (dto.getReputacao() != null) {
            usuario.setReputacao(dto.getReputacao());
        }

        if (dto.getRole() != null) {
            usuario.setRole(formatarPapel(dto.getRole()));
        }

        usuarioRepository.save(usuario);
        return Optional.of(converterParaDTO(usuario));
    }

public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto) {
    Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    usuario.setNome(dto.getNome());
    usuario.setEmail(dto.getEmail());

    if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
        usuario.setSenha(criptografarSenha(dto.getSenha()));
    }

    usuario.setReputacao(dto.getReputacao());
    usuario.setNivel(dto.getNivel());

    if (dto.getRole() != null) { // ✅ corrigido
        usuario.setRole(formatarPapel(dto.getRole()));
    }

    Usuario atualizado = usuarioRepository.save(usuario);
    return converterParaDTO(atualizado);
    }

    public boolean existePorId(Long id) {
    return usuarioRepository.existsById(Objects.requireNonNull(id));
    }

    public Usuario buscarUsuarioPorId(Long id) {
    return usuarioRepository.findById(Objects.requireNonNull(id))
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public int contarUsuarios() {
    return Math.toIntExact(usuarioRepository.count());
    }

    public double mediaReputacao() {
    List<Usuario> usuarios = usuarioRepository.findAll();
    if (usuarios.isEmpty()) return 0.0;
    double soma = usuarios.stream()
                          .mapToDouble(Usuario::getReputacao)
                          .sum();
    return soma / usuarios.size();
    }

    public void alterarPapel(Long id, Role novoPapel) {
    Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    usuario.setRole(novoPapel);
    usuarioRepository.save(usuario);
    }

        // 🔧 Novo método para atualizar senha
public boolean atualizarSenha(Long id, String senhaAtual, String senhaNova) {
    // ICP-07: Rotação de senha exige validação da senha antiga antes da escrita do novo hash.
    Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
        throw new RuntimeException("Senha atual incorreta.");
    }

    usuario.setSenha(passwordEncoder.encode(senhaNova));
    usuarioRepository.save(usuario);
    return true;
    }
}