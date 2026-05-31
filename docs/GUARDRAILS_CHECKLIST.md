# 📋 Checklist de Guardrails para Novos Endpoints

Este documento ajuda a garantir que **todos os novos endpoints** sigam os padrões de segurança e resiliência do projeto.

---

## ✅ Checklist ao Criar um Novo Endpoint

### 1. **DTO com Validação**
- [ ] DTO possui anotações Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, etc).
- [ ] Mensagens de erro em português claro.
- [ ] Ranges de valores estão corretos (ex: `@Min`, `@Max` para numéricos).

**Exemplo:**
```java
public class MeuNovoDTO {
    @NotBlank(message = "Nome é obrigatório.")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @Positive(message = "Valor deve ser maior que zero.")
    private double valor;
}
```

---

### 2. **Controller com @Valid**
- [ ] Endpoint recebe DTO com `@Valid @RequestBody`.
- [ ] Erros de validação são capturados automaticamente por `GlobalExceptionHandler`.
- [ ] **NÃO** fazer validação manual no controller (é redundante).

**Exemplo:**
```java
@PostMapping("/novo-recurso")
public ResponseEntity<?> criarRecurso(@Valid @RequestBody MeuNovoDTO dto) {
    // Validação já foi feita — dto é garantidamente válido aqui
    MinhaEntidade criada = service.criar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(criada);
}
```

---

### 3. **Tratamento de Exceções**
- [ ] Service lança `IllegalArgumentException` para erros de negócio.
- [ ] Service lança `DataIntegrityViolationException` para violações de banco.
- [ ] **NÃO** fazer try-catch no controller — deixe `GlobalExceptionHandler` tratar.

**Exemplo:**
```java
@Service
public class MeuService {
    public MinhaEntidade criar(MeuNovoDTO dto) {
        // Erro de negócio
        if (condicao_invalida) {
            throw new IllegalArgumentException("Motivo específico do erro.");
        }

        // Deixa exception de banco ser capturada por GlobalExceptionHandler
        return repository.save(entidade);
    }
}
```

---

### 4. **Rate Limiting (Automático)**
- [ ] ✅ **Automático** — Todos os endpoints passam por `RateLimitFilter`.
- [ ] Nada a fazer, está aplicado globalmente.
- [ ] Se precisar de limite customizado por endpoint, adicionar anotação futura.

---

### 5. **Timeouts e Circuit Breaker (Futuro)**
- [ ] Se endpoint chama Redis: adicionar `@CircuitBreaker(name = "redis")` e `@TimeLimiter(name = "redis")`.
- [ ] Se endpoint chama blockchain: adicionar `@CircuitBreaker(name = "blockchain")`.
- [ ] Se endpoint é leitura de BD: adicionar `@CircuitBreaker(name = "database")`.

**Exemplo:**
```java
@PostMapping("/transacao-blockchain")
@CircuitBreaker(name = "blockchain")
@TimeLimiter(name = "blockchain")
@Retry(name = "blockchain")
public ResponseEntity<?> registrar(@Valid @RequestBody TransacaoBlockchainDTO dto) {
    // Protegido contra timeouts e falhas cascata
}
```

---

### 6. **Logging de Ações Sensíveis**
- [ ] Login/Cadastro: registrar sucesso/falha (sem senha).
- [ ] Mudanças de role: registrar quem fez, quando, para quem.
- [ ] Operações blockchain: registrar hash, endereços, valor (sem privkeys).

**Exemplo:**
```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
    try {
        Usuario usuario = service.autenticar(dto.getEmail(), dto.getSenha());
        log.info("Login bem-sucedido: {}", dto.getEmail());
        // ...
    } catch (Exception e) {
        log.warn("Falha de login para: {}", dto.getEmail());
        throw e;
    }
}
```

---

### 7. **Resposta Padronizada**
- [ ] Usar `ResponseEntity.status(HttpStatus.XXX).body(dto)`.
- [ ] **NÃO** retornar strings simples ou entidades brutas.
- [ ] Erros são tratados por `GlobalExceptionHandler` — não precisa try-catch.

**Exemplo de Resposta:**
```java
// ✅ Correto
@PostMapping
public ResponseEntity<MinhaEntidadeDTO> criar(@Valid @RequestBody CriacaoDTO dto) {
    MinhaEntidadeDTO criada = service.criar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(criada);
}

// ❌ Incorreto
public ResponseEntity<?> criar(@RequestBody CriacaoDTO dto) {
    try {
        // ... validação manual
        // ... try-catch desnecessário
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
    }
}
```

---

### 8. **Testes Unitários**
- [ ] Testar DTO com dados inválidos → validação falha.
- [ ] Testar service com dados válidos → sucesso.
- [ ] Testar service com dados duplicados → `DataIntegrityViolationException`.
- [ ] Testar controller com JWT inválido → 401 (via `JwtFiltro`).

**Exemplo:**
```java
@Test
void testCriarComDadosInvalidos() {
    MeuNovoDTO dto = new MeuNovoDTO();
    dto.setNome(""); // ❌ Inválido

    assertThrows(MethodArgumentNotValidException.class, () -> {
        mvc.perform(post("/api/novo-recurso")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    });
}
```

---

### 9. **Autorização (Spring Security)**
- [ ] Endpoint público? → `permitAll()` em `SecurityConfig`.
- [ ] Requer autenticação? → `authenticated()` (JWT necessário).
- [ ] Admin-only? → `hasRole("ADMIN")`.

**Exemplo:**
```java
// Em SecurityConfig.filterChain()
.requestMatchers("/api/novo-recurso").hasRole("ADMIN")
// ou
.requestMatchers("/api/novo-publico").permitAll()
```

---

### 10. **Documentação OpenAPI (Swagger)**
- [ ] Adicionar `@Operation` e `@ApiResponse` no endpoint.
- [ ] Descrever parâmetros, possíveis erros (400, 401, 403, 404, 500).

**Exemplo:**
```java
@PostMapping
@Operation(summary = "Cria novo recurso", description = "Requer autenticação")
@ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
@ApiResponse(responseCode = "400", description = "Dados inválidos")
@ApiResponse(responseCode = "409", description = "Recurso duplicado")
public ResponseEntity<MinhaEntidadeDTO> criar(@Valid @RequestBody CriacaoDTO dto) {
    // ...
}
```

---

## 🎯 Template Completo para Novo Endpoint

### 1. DTO
```java
package com.mifica.dto;

import jakarta.validation.constraints.*;

public class NovoDTO {
    @NotBlank(message = "Campo é obrigatório.")
    @Size(min = 3, max = 100)
    private String campo1;

    @Email(message = "Email inválido.")
    private String email;

    @Positive(message = "Valor deve ser positivo.")
    private double valor;

    // Getters e Setters
}
```

### 2. Service
```java
package com.mifica.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NovoService {
    private static final Logger log = LoggerFactory.getLogger(NovoService.class);

    public MinhaEntidade criar(NovoDTO dto) {
        if (condicaoInvalida) {
            throw new IllegalArgumentException("Motivo do erro.");
        }

        MinhaEntidade entidade = new MinhaEntidade();
        entidade.setCampo1(dto.getCampo1());

        log.info("Criando novo recurso: {}", dto.getCampo1());
        return repository.save(entidade);
    }
}
```

### 3. Controller
```java
package com.mifica.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/novo")
public class NovoController {

    private final NovoService novoService;

    public NovoController(NovoService novoService) {
        this.novoService = novoService;
    }

    @PostMapping
    @Operation(summary = "Cria novo recurso")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<MinhaEntidade> criar(@Valid @RequestBody NovoDTO dto) {
        MinhaEntidade criada = novoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }
}
```

### 4. Teste
```java
package com.mifica.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NovoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testCriarComDadosValidos() throws Exception {
        NovoDTO dto = new NovoDTO();
        dto.setCampo1("Valor válido");
        dto.setEmail("test@example.com");
        dto.setValor(10.0);

        mvc.perform(post("/api/novo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void testCriarComDadosInvalidos() throws Exception {
        NovoDTO dto = new NovoDTO();
        dto.setCampo1(""); // ❌ Inválido

        mvc.perform(post("/api/novo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(dto)))
            .andExpect(status().isBadRequest());
    }
}
```

---

## 🚀 Fluxo de Implementação

1. **Criar DTO com validações** ← Start here
2. **Criar Service com lógica de negócio**
3. **Criar Controller com @Valid**
4. **Adicionar ao SecurityConfig** (se necessário)
5. **Testar com dados válidos e inválidos**
6. **Documentar com @Operation**
7. **Adicionar anotações de resilience** (se necessário)
8. **Fazer merge e deploy**

---

## 📚 Referências

- [Jakarta Validation](https://jakarta.ee/specifications/validation/)
- [Spring Security](https://spring.io/projects/spring-security)
- [Resilience4j](https://resilience4j.readme.io/)
- [OpenAPI/Swagger](https://swagger.io/)

---

**Última atualização:** 31 de maio de 2026
