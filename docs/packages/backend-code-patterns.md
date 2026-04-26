# Backend Code Patterns — Como Estender o Projeto

Guia de padrões implementados no Mifica para manter consistência e qualidade.

---

## 1. Criar um Novo Serviço (Service)

### Passo a Passo

**Cenário:** Você quer criar um `SolicitacaoCreditoService` para gerenciar solicitações de crédito.

### 1.1 Criar a Interface (Opcional, Recomendado)

```java
// src/main/java/com/mifica/service/SolicitacaoCreditoService.java

package com.mifica.service;

import com.mifica.dto.SolicitacaoCreditoDTO;
import java.util.List;

/**
 * Serviço de solicitações de crédito.
 * 
 * Responsabilidades:
 * - Criar nova solicitação
 * - Listar solicitações de um usuário
 * - Aprovar/rejeitar solicitação
 * - Registrar histórico
 * 
 * ICP-TOTAL: 3
 * ICP-01: Bifurcação de status (PENDENTE, APROVADA, REJEITADA)
 * ICP-02: Busca de usuário por email
 * ICP-03: Persistência em banco
 */
public interface SolicitacaoCreditoService {
    SolicitacaoCreditoDTO criar(String emailUsuario, SolicitacaoCreditoDTO dto);
    List<SolicitacaoCreditoDTO> listar(String emailUsuario);
    SolicitacaoCreditoDTO aprovar(Long id, String motivoAprovacao);
    SolicitacaoCreditoDTO rejeitar(Long id, String motivoRejeicao);
}
```

### 1.2 Implementar o Service

```java
// src/main/java/com/mifica/service/impl/SolicitacaoCreditoServiceImpl.java

package com.mifica.service.impl;

import com.mifica.dto.SolicitacaoCreditoDTO;
import com.mifica.entity.SolicitacaoCredito;
import com.mifica.entity.Usuario;
import com.mifica.repository.SolicitacaoCreditoRepository;
import com.mifica.repository.UsuarioRepository;
import com.mifica.service.SolicitacaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitacaoCreditoServiceImpl implements SolicitacaoCreditoService {

    @Autowired
    private SolicitacaoCreditoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public SolicitacaoCreditoDTO criar(String emailUsuario, SolicitacaoCreditoDTO dto) {
        // Validação
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (dto.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        // Criar entity
        SolicitacaoCredito entidade = new SolicitacaoCredito();
        entidade.setUsuario(usuario);
        entidade.setValor(dto.getValor());
        entidade.setMotivo(dto.getMotivo());
        entidade.setStatus("PENDENTE");
        entidade.setDataSolicitacao(LocalDateTime.now());

        // Persistir
        SolicitacaoCredito salva = repository.save(entidade);

        // Retornar DTO
        return toDTO(salva);
    }

    @Override
    public List<SolicitacaoCreditoDTO> listar(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return repository.findByUsuario(usuario).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public SolicitacaoCreditoDTO aprovar(Long id, String motivoAprovacao) {
        SolicitacaoCredito entidade = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        entidade.setStatus("APROVADA");
        entidade.setMotivoAprovacao(motivoAprovacao);
        entidade.setDataAprovacao(LocalDateTime.now());

        SolicitacaoCredito atualizada = repository.save(entidade);
        return toDTO(atualizada);
    }

    @Override
    public SolicitacaoCreditoDTO rejeitar(Long id, String motivoRejeicao) {
        SolicitacaoCredito entidade = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        entidade.setStatus("REJEITADA");
        entidade.setMotivoRejeicao(motivoRejeicao);

        SolicitacaoCredito atualizada = repository.save(entidade);
        return toDTO(atualizada);
    }

    // Método auxiliar de conversão
    private SolicitacaoCreditoDTO toDTO(SolicitacaoCredito entidade) {
        SolicitacaoCreditoDTO dto = new SolicitacaoCreditoDTO();
        dto.setId(entidade.getId());
        dto.setValor(entidade.getValor());
        dto.setStatus(entidade.getStatus());
        dto.setDataSolicitacao(entidade.getDataSolicitacao());
        return dto;
    }
}
```

### 1.3 Injetar no Controller

```java
@RestController
@RequestMapping("/api/solicitacoes-credito")
public class SolicitacaoCreditoController {

    @Autowired
    private SolicitacaoCreditoService solicitacaoService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(
        @RequestHeader(value = "Authorization") String token,
        @RequestBody SolicitacaoCreditoDTO dto) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            SolicitacaoCreditoDTO criada = solicitacaoService.criar(email, dto);
            return ResponseEntity.ok(criada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
```

---

## 2. Criar um Novo Controller

### Template

```java
package com.mifica.controller;

import com.mifica.dto.*;
import com.mifica.service.*;
import com.mifica.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para [RECURSO].
 * 
 * Endpoints:
 * - GET  /api/[recurso]           → Listar todos
 * - GET  /api/[recurso]/{id}      → Obter por ID
 * - POST /api/[recurso]           → Criar
 * - PUT  /api/[recurso]/{id}      → Atualizar
 * - DELETE /api/[recurso]/{id}    → Deletar
 */
@RestController
@RequestMapping("/api/[recurso]")
public class [RecursoCapitalizado]Controller {

    // ICP-TOTAL: [Estimar aqui]

    @Autowired
    private [RecursoCapitalizado]Service service;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao listar: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obterPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(
        @RequestHeader(value = "Authorization") String token,
        @RequestBody [RecursoCapitalizado]DTO dto) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            [RecursoCapitalizado]DTO criado = service.criar(email, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizar(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization") String token,
        @RequestBody [RecursoCapitalizado]DTO dto) {
        try {
            String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
            [RecursoCapitalizado]DTO atualizado = service.atualizar(id, email, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        }
    }
}
```

---

## 3. Criar uma Nova Entity (JPA)

### Template

```java
package com.mifica.entity;

import org.hibernate.envers.Audited;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity [NOME].
 * 
 * Mapeamento JPA:
 * - Tabela: [nome_minusculo]
 * - Auditada com Hibernate Envers
 * - Relacionamento com Usuario (many-to-one)
 */
@Entity
@Table(name = "[nome_minusculo]")
@Audited // Ativar auditoria de mudanças
public class [NomeCapitalizado] {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
```

### Com Validações

```java
@Entity
@Table(name = "minhas_entidades")
@Audited
public class MinhaEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 100, message = "Tamanho entre 3 e 100 caracteres")
    private String nome;

    @Column(nullable = false)
    @Min(0)
    @Max(1000)
    private int pontos;

    // Getters & Setters
}
```

---

## 4. Criar um novo DTO

### Template Simples

```java
package com.mifica.dto;

public class MinhaDTO {
    private Long id;
    private String nome;
    private int valor;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
}
```

### Com Validações (Recomendado)

```java
package com.mifica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

public class MinhaDTO {

    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    @JsonProperty("nome")
    private String nome;

    @Min(value = 0, message = "Valor deve ser >= 0")
    @Max(value = 1000000, message = "Valor deve ser <= 1000000")
    @JsonProperty("valor")
    private int valor;

    public MinhaDTO() {}

    public MinhaDTO(Long id, String nome, int valor) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
}
```

---

## 5. Criar um novo Repository

### Template

```java
package com.mifica.repository;

import com.mifica.entity.MinhaEntidade;
import com.mifica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MinhaEntidadeRepository extends JpaRepository<MinhaEntidade, Long> {

    // Métodos automáticos (Spring Data)
    Optional<MinhaEntidade> findById(Long id);
    List<MinhaEntidade> findAll();
    <S extends MinhaEntidade> S save(S entity);
    void deleteById(Long id);

    // Métodos customizados
    List<MinhaEntidade> findByUsuario(Usuario usuario);

    // Com Query JPQL
    @Query("SELECT e FROM MinhaEntidade e WHERE e.usuario = :usuario AND e.ativo = true")
    List<MinhaEntidade> buscarAtivos(@Param("usuario") Usuario usuario);

    // Com SQL nativo
    @Query(value = "SELECT * FROM minhas_entidades WHERE usuario_id = ? AND valor > ?", nativeQuery = true)
    List<MinhaEntidade> buscarComValorAlto(Long usuarioId, int valorMinimo);
}
```

---

## 6. Tratamento de Exceções

### Padrão Recomendado

```java
@PostMapping("/criar")
public ResponseEntity<?> criar(@RequestBody MinhaDTO dto) {
    try {
        // Validação
        if (dto == null) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("DTO não pode ser nulo", 400));
        }

        // Lógica
        MinhaDTO resultado = service.criar(dto);

        // Sucesso
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

    } catch (IllegalArgumentException e) {
        // Erro de validação de negócio
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage(), 400));

    } catch (DataIntegrityViolationException e) {
        // Erro de banco (ex: unique constraint)
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("Dados duplicados ou inválidos", 409));

    } catch (Exception e) {
        // Erro inesperado
        log.error("Erro ao criar", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Erro interno do servidor", 500));
    }
}

// Helper: classe ErrorResponse
public class ErrorResponse {
    private String mensagem;
    private int codigo;
    private long timestamp;

    public ErrorResponse(String mensagem, int codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
}
```

---

## 7. Logging (SLF4J + Logback)

### Uso Correto

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MeuService {

    private static final Logger log = LoggerFactory.getLogger(MeuService.class);

    public void fazerAlgo() {
        log.info("Iniciando operação");

        try {
            // código
            log.debug("Valor de debug: {}", valor);
        } catch (Exception e) {
            log.error("Erro ao fazer algo", e);
        }

        log.info("Operação concluída");
    }
}
```

### Níveis de Log

- `log.debug()` — Informações detalhadas para debug
- `log.info()` — Eventos normais do programa
- `log.warn()` — Algo inesperado, mas o programa continua
- `log.error()` — Erro, programa pode ser impactado

---

## 8. Padrão de Validação com CDD/ICP

### Antes (Sem CDD)

```java
public void criar(String email, DTO dto) {
    if (email == null) throw ...
    if (dto == null) throw ...
    if (dto.getNome() == null) throw ...
    if (dto.getNome().length() < 3) throw ...
    if (dto.getValor() < 0) throw ...
    // 50+ linhas de validação
}
```

**ICP alto, difícil de ler.**

### Depois (Com Validator)

```java
// Separar validação em classe específica
@Component
public class MeuDTOValidator {
    public void validar(String email, MeuDTO dto) {
        if (email == null) throw new IllegalArgumentException("Email obrigatório");
        if (dto == null) throw new IllegalArgumentException("DTO não pode ser nulo");
        validarNome(dto.getNome());
        validarValor(dto.getValor());
    }

    private void validarNome(String nome) {
        if (nome == null || nome.length() < 3) {
            throw new IllegalArgumentException("Nome deve ter 3+ caracteres");
        }
    }

    private void validarValor(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
    }
}

// Usar no service
@Service
public class MeuService {
    @Autowired
    private MeuDTOValidator validator;

    public void criar(String email, MeuDTO dto) {
        validator.validar(email, dto);
        // resto da lógica
    }
}
```

**ICP menor, código mais legível.**

---

## 9. Estrutura de Resposta Padronizada

### Template

```java
public class ApiResponse<T> {
    private boolean sucesso;
    private String mensagem;
    private T dados;
    private long timestamp;

    public ApiResponse(boolean sucesso, String mensagem, T dados) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public T getDados() { return dados; }
    public long getTimestamp() { return timestamp; }
}
```

### Uso

```java
@GetMapping("/perfil")
public ResponseEntity<ApiResponse<UsuarioDTO>> obterPerfil(
    @RequestHeader(value = "Authorization") String token) {
    
    try {
        String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
        UsuarioDTO usuario = service.obterPerfil(email);
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Perfil obtido com sucesso", usuario)
        );
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, e.getMessage(), null));
    }
}
```

---

## 10. Checklist de Antes de Fazer PR

- [ ] Código segue padrões SOLID
- [ ] Não adicionou pontos desnecessários de ICP
- [ ] Métodos têm <50 linhas
- [ ] Validações estão centralizadas
- [ ] Logging adequado (não silencioso)
- [ ] Testes unitários cobrem casos principais
- [ ] Nenhuma hardcoding de valores
- [ ] Usa variáveis de ambiente para config
- [ ] Comentários `// ICP-XX` se complexidade > 3
- [ ] `git diff` revisado antes de push

---

## Referências Rápidas

- [Documentação Spring](https://spring.io/projects/spring-boot)
- [JPA/Hibernate](https://hibernate.org/orm/)
- [Backend Entity Docs](./backend-entity.md)
- [Backend Service Docs](./backend-service.md)
- [CDD Analysis](./backend-cdd-analysis.md)

