package com.mifica.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.mifica.dto.EstatisticasDTO;
import com.mifica.dto.LoginDTO;
import com.mifica.dto.UsuarioDTO;
import com.mifica.entity.Role;
import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;
import com.mifica.service.EmailService;
import com.mifica.service.EmailVerificationService;
import com.mifica.service.UsuarioService;
import com.mifica.util.JwtUtil;
import com.mifica.redis.GamificationPublisher;

/**
 * Controller principal de usuários — gerencia cadastro, login, perfil,
 * gamificação, reputação, conquistas e operações administrativas.
 *
 * Todos os endpoints começam com /api/usuarios.
 * Autenticação via JWT no header Authorization: Bearer {token}.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // ICP-TOTAL: 6
    // ICP-01: Orquestra múltiplos fluxos (cadastro, verificação de e-mail, login, perfil e administração) no mesmo controller.

    private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final String TOKEN_INVALIDO = "Token inválido ou expirado.";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Value("${admin.cadastro.senha}")
    private String senhaCadastroAdmin;

    @PostMapping("/validar-acesso-admin")
    public ResponseEntity<?> validarAcessoAdmin(@RequestBody Map<String, String> payload) {
        String senhaAcesso = payload.get("senhaAcesso");
        if (senhaAcesso == null || !senhaAcesso.trim().equals(senhaCadastroAdmin.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha de acesso inválida.");
        }
        return ResponseEntity.ok("Acesso autorizado.");
    }

    // 🔧 Teste Swagger
    @GetMapping("/teste-swagger")
    public ResponseEntity<String> testarSwagger() {
        return ResponseEntity.ok("Swagger reconheceu o controller!");
    }

    // 🔧 Cadastro de usuário comum
    /**
     * Cadastro público de usuário.
     * Verifica duplicidade de email antes de criar a conta.
     * Senha é criptografada com BCrypt no UsuarioService.
     */
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto) {
        // ICP-02: Cadastro possui bifurcação para reenvio de confirmação e tratamento de consistência transacional de e-mail.
        if (dto.getEmail() != null) {
            dto.setEmail(dto.getEmail().trim().toLowerCase());
        }

        if (usuarioService.emailJaExiste(dto.getEmail())) {
            Usuario existente = usuarioService.buscarPorEmail(dto.getEmail());

            if (existente != null && !Boolean.TRUE.equals(existente.getEmailVerificado())) {
                try {
                    String token = emailVerificationService.gerarToken(existente);
                    emailService.enviarEmailVerificacao(existente.getEmail(), existente.getNome(), token);
                    return ResponseEntity.ok(Map.of(
                            "email", existente.getEmail(),
                            "mensagem", "Conta já existe e ainda não foi verificada. Enviamos um novo e-mail de confirmação."
                    ));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Conta já existe, mas não foi possível reenviar o e-mail de confirmação agora.");
                }
            }

            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }
        UsuarioDTO novo;
        try {
            novo = usuarioService.criar(dto);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }

        Usuario usuarioCriado = usuarioService.buscarPorEmail(dto.getEmail());

        try {
            String token = emailVerificationService.gerarToken(usuarioCriado);
            emailService.enviarEmailVerificacao(usuarioCriado.getEmail(), usuarioCriado.getNome(), token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Conta criada, mas não foi possível enviar o e-mail de confirmação. Use reenviar confirmação.");
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", novo.getId());
        resposta.put("email", novo.getEmail());
        resposta.put("mensagem", "Cadastro realizado! Verifique seu e-mail para ativar a conta.");
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<String> verificarEmail(@RequestParam("token") String token) {
        String erro = emailVerificationService.verificarToken(token);
        if (erro != null) {
            return ResponseEntity.badRequest().body(erro);
        }
        return ResponseEntity.ok("✅ E-mail verificado com sucesso! Agora você já pode fazer login.");
    }

    @PostMapping("/reenviar-confirmacao")
    public ResponseEntity<String> reenviarConfirmacao(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return ResponseEntity.ok("Se o e-mail existir, enviaremos uma nova confirmação.");
        }

        if (Boolean.TRUE.equals(usuario.getEmailVerificado())) {
            return ResponseEntity.ok("Este e-mail já está verificado.");
        }

        try {
            String token = emailVerificationService.gerarToken(usuario);
            emailService.enviarEmailVerificacao(usuario.getEmail(), usuario.getNome(), token);
            return ResponseEntity.ok("Novo e-mail de confirmação enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível reenviar a confirmação agora. Tente novamente em instantes.");
        }
    }

    // 🔧 Cadastro de administrador
    /**
     * Cadastro de administrador (protegido por senha de acesso).
     * Cria usuário com role ADMIN e reputação inicial de 100.
     */
    @PostMapping("/cadastro-admin")
    public ResponseEntity<?> cadastrarAdmin(@RequestBody Map<String, Object> payload) {
        String senhaAcesso = (String) payload.get("senhaAcesso");
        if (senhaAcesso == null || !senhaAcesso.trim().equals(senhaCadastroAdmin.trim())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha de acesso inválida.");
        }

        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome((String) payload.get("nome"));
        novoAdmin.setEmail((String) payload.get("email"));
        novoAdmin.setSenha(passwordEncoder.encode((String) payload.get("senha")));
        novoAdmin.setEnabled(Boolean.TRUE);
        novoAdmin.setEmailVerificado(Boolean.TRUE);
        novoAdmin.setRole(Role.ROLE_ADMIN);
        novoAdmin.setReputacao(100);
        novoAdmin.setConquistas(new ArrayList<>());
        novoAdmin.setTelefone((String) payload.get("telefone"));

        String dataStr = (String) payload.get("dataNascimento");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        novoAdmin.setDataNascimento(LocalDate.parse(dataStr, formatter));

        usuarioRepository.save(novoAdmin);
        return ResponseEntity.ok("Administrador cadastrado com sucesso");
    }

 // 🔧 Login com JWT via POST (frontend/app)
    /**
     * Autenticação de usuário — valida credenciais e retorna token JWT.
     * O frontend armazena o token e envia no header Authorization.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginPost(@RequestBody LoginDTO dto) {
        // ICP-03: Login combina validação de credenciais, geração de JWT e montagem de payload para frontend.
        try {
            boolean valido = usuarioService.validarLogin(dto.getEmail(), dto.getSenha());
            if (!valido) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
            }

            Usuario usuario = usuarioService.buscarPorEmail(dto.getEmail());
            // Gera token JWT assinado com HMAC-SHA256 contendo email e role
            String token = jwtUtil.gerarToken(usuario.getEmail());

            // Retorna token + dados do usuário para o frontend armazenar
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("token", token);
            resposta.put("id", usuario.getId());
            resposta.put("nome", usuario.getNome());
            resposta.put("reputacao", usuario.getReputacao());
            resposta.put("conquistas", usuario.getConquistas());
            resposta.put("emailVerificado", usuario.getEmailVerificado());

            return ResponseEntity.ok(resposta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 🔧 Listar todos (ADMIN)
    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // 🔧 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.buscarPorId(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO));
    }

    // 🔧 Perfil do usuário autenticado
    /**
     * Retorna o perfil do usuário autenticado.
     * Extrai o email do token JWT e busca os dados no banco.
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(@RequestHeader("Authorization") String token) {
        // ICP-04: Extração de identidade via JWT com tratamento explícito de token inválido e usuário inexistente.
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO);
            }
            UsuarioDTO dto = usuarioService.converterParaDTO(usuario);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO atualizado = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    // 🔧 Atualizar reputação
    @PatchMapping("/perfil/reputacao")
    public ResponseEntity<String> atualizarReputacao(@RequestHeader("Authorization") String token,
                                                     @RequestBody int novaReputacao) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.atualizarReputacao(email, novaReputacao);
            return ResponseEntity.ok("Reputação atualizada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Missão diária
    @GetMapping("/perfil/missao-diaria")
    public ResponseEntity<String> verificarMissaoDiaria(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NAO_ENCONTRADO);
            }
            boolean cumpriu = usuarioService.verificarMissaoDiaria(usuario);
            return ResponseEntity.ok(cumpriu
                ? "✅ Missão diária cumprida!"
                : "❌ Missão diária pendente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Recompensas
    @PostMapping("/perfil/recompensas")
    public ResponseEntity<String> aplicarRecompensas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.aplicarRecompensas(email);
            return ResponseEntity.ok("Recompensas aplicadas com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 Conquistas
    @GetMapping("/perfil/conquistas")
    public ResponseEntity<List<String>> listarConquistas(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            List<String> conquistas = usuarioService.listarConquistas(email);
            return ResponseEntity.ok(conquistas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 🔧 Deletar conta
    @DeleteMapping("/perfil")
    public ResponseEntity<String> deletarConta(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            usuarioService.deletarPorEmail(email);
            return ResponseEntity.ok("Conta excluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALIDO);
        }
    }

    // 🔧 HEAD para verificar existência
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> verificarExistencia(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return existe ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // 🔧 OPTIONS
    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET,POST,PUT,PATCH,DELETE,HEAD,OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    // 🔧 Estatísticas
    @GetMapping("/estatisticas")
    public EstatisticasDTO getEstatisticas() {
        int totalUsuarios = usuarioService.contarUsuarios();
        double mediaReputacao = usuarioService.mediaReputacao();
        return new EstatisticasDTO(totalUsuarios, mediaReputacao);
    }

@PutMapping("/{id}/senha")
public ResponseEntity<?> atualizarSenha(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload,
        @RequestHeader("Authorization") String token) {
    // ICP-05: Alteração de senha depende de autorização por identidade do token e validações de domínio.
    try {
        String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null || !usuario.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não autorizado.");
        }

        String senhaAtual = payload.get("senhaAtual");
        String senhaNova = payload.get("senhaNova");

        usuarioService.atualizarSenha(id, senhaAtual, senhaNova);

        return ResponseEntity.ok("Senha atualizada com sucesso!");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    private final GamificationPublisher publisher;

    @Autowired
    public UsuarioController(GamificationPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Publica evento de pontuação no Redis Pub/Sub.
     * O GamificationSubscriber recebe e processa os pontos de forma assíncrona.
     */
    @PostMapping("/{id}/points")
    public ResponseEntity<String> addPoints(@PathVariable Long userId, @RequestParam int points) {
        // ICP-06: Publicação assíncrona desacopla request HTTP do processamento de pontuação.
        // Publica mensagem no canal Redis — processada assincronamente pelo subscriber
        publisher.publishEvent(userId, points);
        return ResponseEntity.ok("📤 Evento de pontos enviado via Redis para usuário " + userId);
    }


}

