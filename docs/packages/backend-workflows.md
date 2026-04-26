# Backend Workflows — Fluxos Críticos e Diagramas

Documentação dos fluxos principais do backend Mifica com diagramas e sequências.

---

## 1. Fluxo de Autenticação (Login)

### Diagrama

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐         ┌─────────────┐
│   Frontend  │         │   Backend    │         │    MySQL     │         │  JWT Token  │
└──────┬──────┘         └──────┬───────┘         └──────┬───────┘         └──────┬──────┘
       │                       │                        │                         │
       │  POST /api/usuarios/login                      │                         │
       │  { email, senha }                              │                         │
       │──────────────────────────────────────────────>│                         │
       │                       │                        │                         │
       │                       │  SELECT * FROM usuario WHERE email = ? │         │
       │                       ├───────────────────────────────────────>│         │
       │                       │<────────────────────────── Usuario     │         │
       │                       │                                         │         │
       │  BCryptPasswordEncoder.matches()               │                         │
       │  (valida senha)                                │                         │
       │                       │                        │                         │
       │                       │  ✓ Senha válida        │                         │
       │                       │  Gera token JWT        │                         │
       │                       ├─────────────────────────────────────────────────>│
       │                       │  Token assinado com secret do JWT       │         │
       │<──────────────────────┼─────────────────────────────────────────────────┤
       │  200 OK + Token       │                        │                         │
       │  { token, email }     │                        │                         │
       │                       │                        │                        
```

### Sequência de Passos

1. **Cliente** envia `POST /api/usuarios/login` com `{email, senha}`
2. **UsuarioController** recebe requisição
3. **UsuarioService.validarLogin()** busca usuário por email no MySQL
4. **BCryptPasswordEncoder.matches()** compara hash da senha
5. Se senha válida → **JwtUtil.gerarToken()** cria JWT assinado
6. Backend retorna `{token, email, reputacao, ...}`
7. **Cliente** armazena token em `localStorage`
8. Próximas requisições usam `Authorization: Bearer {token}`

### Código Principal

**Controller:**
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
    if (!usuarioService.validarLogin(dto.getEmail(), dto.getSenha())) {
        return ResponseEntity.badRequest().body("Credenciais inválidas");
    }
    
    String token = jwtUtil.gerarToken(dto.getEmail());
    // retorna token + user info
}
```

**Service:**
```java
public boolean validarLogin(String email, String senhaDigitada) {
    Usuario usuario = buscarPorEmail(email);
    if (!passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
        throw new RuntimeException("Senha inválida");
    }
    return true;
}
```

---

## 2. Fluxo de Proteção com JWT (Requisição Autenticada)

### Diagrama

```
┌─────────────┐         ┌────────────────┐         ┌──────────────┐         ┌──────────┐
│   Frontend  │         │   JwtFiltro    │         │   Validator  │         │ Endpoint │
└──────┬──────┘         └────────┬───────┘         └──────┬───────┘         └────┬─────┘
       │                         │                        │                       │
       │  GET /api/usuarios/perfil                        │                       │
       │  Authorization: Bearer {token}                   │                       │
       ├────────────────────────────────────────────────>│                       │
       │                         │                        │                       │
       │                         │ Extrai token do header │                       │
       │                         │                        │                       │
       │                         │  Valida assinatura JWT │                       │
       │                         ├───────────────────────>│                       │
       │                         │<────────────────────── ✓ Válido               │
       │                         │                        │                       │
       │                         │  Extrai claims         │                       │
       │                         │  { sub: email }        │                       │
       │                         │                        │                       │
       │                         │ SecurityContext.setAuthentication()            │
       │                         │                        │                       │
       │                         │ Request prossegue      │                       │
       │                         ├───────────────────────────────────────────────>│
       │                         │                        │                       │
       │                         │                        │   Service executa     │
       │                         │                        │   controller delegates│
       │<────────────────────────┼────────────────────────┼──────────────────────┤
       │  200 OK + Perfil        │                        │                       │
```

### Checklist de Proteção

- ✅ Requisição chega com header `Authorization: Bearer {token}`
- ✅ `JwtFiltro` extrai token
- ✅ `JwtService.validarToken()` valida assinatura
- ✅ Se inválido → erro 401 Unauthorized
- ✅ Se válido → email extraído do `claims`
- ✅ `SecurityContext` atualizado
- ✅ Endpoint executado com autenticação

### Código Principal

**Filtro:**
```java
@Override
protected void doFilterInternal(HttpServletRequest request, ...) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        try {
            Claims claims = jwtService.validarToken(token);
            // Atualizar SecurityContext
            SecurityContextHolder.getContext().setAuthentication(...);
        } catch (JwtException e) {
            // Erro 401
        }
    }
    filterChain.doFilter(request, response);
}
```

---

## 3. Fluxo de Gamificação (Pub/Sub Redis)

### Diagrama

```
┌──────────────────┐         ┌─────────────────┐         ┌──────────────┐
│  Evento Interno  │         │   Redis Pub/Sub │         │   Subscriber │
│  (ex: Badge)     │         │   (Upstash)     │         │              │
└────────┬─────────┘         └────────┬────────┘         └──────┬───────┘
         │                            │                         │
         │ GamificationPublisher      │                         │
         │ .publicarEventoGamificacao()                         │
         │                            │                         │
         │ Formata mensagem           │                         │
         │ "User:123 Points:50"       │                         │
         │                            │                         │
         │ PUBLISH gamification-events │                        │
         ├───────────────────────────>│                         │
         │                            │                         │
         │                            │ Redis encaminha msg     │
         │                            │ para listeners           │
         │                            ├────────────────────────>│
         │                            │                         │
         │                            │  GamificationSubscriber │
         │                            │  .onMessage()           │
         │                            │                         │
         │                            │  Parse "User:123 Points:50"
         │                            │                         │
         │                            │  Chama GamificationService
         │                            │  .addPoints(123, 50)    │
         │                            │                         │
         │                            │  Persiste em MySQL      │
         │                            │  Descobri badges        │
         │                            │                         │
         │                            │  Armazena msg em buffer │
         │                            │  (últimas 50 msgs)      │
         │                            │                         │
         │                            │ ✅ Processado           │
         │                            │                         │
```

### Fluxo Passo a Passo

1. **Evento de gamificação** ocorre (ex: usuário completa desafio)
2. **GamificationPublisher.publicarEventoGamificacao()** é chamado
3. Publisher **formata a mensagem**: `"User:{userId} Points:{points}"`
4. **Redis PUBLISH** coloca mensagem no canal `gamification-events`
5. **GamificationSubscriber** escuta canal via **RedisMessageListenerContainer**
6. Subscriber **recebe mensagem** automaticamente
7. **onMessage()** faz parse da mensagem
8. Chama **GamificationService.addPoints()** para persistir
9. Service **busca usuário** no banco
10. **Atualiza pontos** e descobre **badges desbloqueadas**
11. Mensagem é **armazenada em buffer circular** (máx 50)
12. Retorna sucesso

### Quando Usar Pub/Sub

✅ **Use** quando:
- Operação pode ser **assíncrona** (não precisa resposta imediata)
- Precisa **desacoplar** componentes
- Quer **escalabilidade** (múltiplos subscribers)

❌ **Não use** quando:
- Precisa resposta **síncrona** (ex: login)
- Operação é **crítica** (não pode falhar silenciosamente)

### Código Principal

**Publisher:**
```java
@Component
public class GamificationPublisher {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void publicarEventoGamificacao(Long userId, int points) {
        String mensagem = "User:" + userId + " Points:" + points;
        redisTemplate.convertAndSend("gamification-events", mensagem);
    }
}
```

**Subscriber:**
```java
@Component
public class GamificationSubscriber {
    public void onMessage(String message) {
        // Parse: "User:123 Points:50"
        String[] parts = message.split(" ");
        Long userId = Long.parseLong(parts[0].split(":")[1]);
        int points = Integer.parseInt(parts[1].split(":")[1]);
        
        gamificationService.addPoints(userId, points);
    }
}
```

---

## 4. Fluxo de Transação Blockchain

### Diagrama

```
┌───────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Usuário A   │         │  BlockchainSvc   │         │   MySQL      │
│   (Admin)     │         │                  │         │ (Blockchain) │
└───────┬───────┘         └────────┬─────────┘         └──────┬───────┘
        │                          │                          │
        │ POST /api/blockchain     │                          │
        │ { destinatario, valor }  │                          │
        ├──────────────────────────>│                          │
        │                          │                          │
        │                          │ Validar remetente        │
        │                          │ Validar destinatário     │
        │                          │ Validar valor > 0        │
        │                          │                          │
        │                          │ Se Admin, verificar      │
        │                          │ limite de movimentação    │
        │                          │                          │
        │                          │ SELECT SUM(valor)        │
        │                          │ FROM transacao_blockchain│
        │                          ├─────────────────────────>│
        │                          │<───── totalMovimentado   │
        │                          │                          │
        │                          │ totalMovimentado + valor │
        │                          │ vs LIMITE (1M)           │
        │                          │                          │
        │                          │ ✓ Dentro do limite       │
        │                          │                          │
        │                          │ Gerar hash aleatório     │
        │                          │ (ou usar fornecido)      │
        │                          │                          │
        │                          │ INSERT transacao         │
        │                          │ { hash, remetente,       │
        │                          │   destinatario, valor,   │
        │                          │   dataCriacao }          │
        │                          ├─────────────────────────>│
        │                          │<───── transacao salva    │
        │                          │                          │
        │<──────────────────────────┤                          │
        │ 201 Created              │                          │
        │ { id, hash, ... }        │                          │
```

### Validações Implementadas

1. **DTO não nulo** → erro se `null`
2. **Destinatário obrigatório** → erro se vazio
3. **Valor > 0** → erro se ≤ 0
4. **Remetente existe** → erro se não encontrado
5. **Destinatário existe** → erro se não encontrado
6. **Regra de transferência** → usuário comum não pode transferir para admin
7. **Limite de admin** → se remetente é admin, verificar limite de 1M total

### Código Principal

```java
public TransacaoBlockchainDTO registrarTransacao(...) {
    // Validações
    if (dto == null) throw ...
    if (dto.getDestinatario() == null) throw ...
    if (dto.getValor() <= 0) throw ...

    Usuario remetente = usuarioRepository.findByEmail(emailRemetente)
        .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado"));

    Usuario destinatario = usuarioRepository.findByEmail(dto.getDestinatario().trim())
        .orElseThrow(() -> new IllegalArgumentException("Destinatário não encontrado"));

    // Regra: usuário comum não pode transferir para admin
    if (!remetenteEhAdmin && destinatarioEhAdmin) {
        throw new IllegalArgumentException("Regra de transferência violada");
    }

    // Limite de admin
    if (remetenteEhAdmin) {
        double totalJaMovimentado = transacaoRepo.somarValorMovimentadoPorRemetente(...);
        if ((totalJaMovimentado + dto.getValor()) > LIMITE_MOVIMENTACAO_ADMIN) {
            throw new IllegalArgumentException("Limite excedido");
        }
    }

    // Persistir
    TransacaoBlockchain transacao = new TransacaoBlockchain();
    transacao.setHashTransacao(dto.getHashTransacao() ?? UUID.randomUUID().toString());
    transacao.setRemetente(remetente.getEmail());
    transacao.setDestinatario(destinatario.getEmail());
    transacao.setValor(dto.getValor());
    transacao.setDataTransacao(LocalDateTime.now());

    return toDTO(transacaoRepo.save(transacao));
}
```

---

## 5. Fluxo de Reputação e Desafios

### Diagrama

```
┌──────────────────────┐         ┌─────────────────┐         ┌──────────────┐
│   Usuário Completa   │         │ DesafioService  │         │ ReputacaoSvc │
│   Desafio            │         │                 │         │              │
└──────────┬───────────┘         └────────┬────────┘         └──────┬───────┘
           │                              │                        │
           │ POST /api/desafios/          │                        │
           │ {id, prova}                  │                        │
           ├─────────────────────────────>│                        │
           │                              │                        │
           │                              │ Validar desafio        │
           │                              │ Validar prova          │
           │                              │                        │
           │                              │ ✓ Desafio concluído    │
           │                              │                        │
           │                              │ Calcular pontos        │
           │                              │ Descobre badges?       │
           │                              │                        │
           │                              │ Atualizar reputação    │
           │                              ├───────────────────────>│
           │                              │                        │
           │                              │  Registrar histórico   │
           │                              │  { email, repAnt, repNova }
           │                              │  em HistoricoReputacao │
           │                              │                        │
           │                              │  Salvar usuário        │
           │                              │  com nova reputação    │
           │<──────────────────────────────┤                        │
           │ 200 OK                        │                        │
           │ { pontos, novaReputacao }     │                        │
```

### Fluxo de Pontos e Badges

1. **Usuário completa desafio** → POST `/api/desafios/{id}/completar`
2. **DesafioController** valida desafio
3. **DesafioService** marca como concluído
4. **GamificationService.addPoints()** é chamado
5. **Calcula pontos** (base 10, multiplicadores por nível)
6. **Atualiza usuário.pontos**
7. **Descobri badges**: itera `usuario.badges` e verifica condições
8. **Se nova badge** → adiciona à lista e notifica
9. **ReputacaoService.registrarAlteracao()** é chamado
10. **Histórico** é criado em `HistoricoReputacao`
11. **Usuário.reputacao** é atualizado
12. **Nível** pode aumentar se reputação >= limiar

---

## 6. Fluxo de Erro (Exception Handling)

### Diagrama

```
┌────────────────┐         ┌──────────────┐         ┌──────────────────┐
│ Request chega  │         │ Controller   │         │  GlobalExHandler │
└────────┬───────┘         └──────┬───────┘         └────────┬─────────┘
         │                        │                          │
         ├───────────────────────>│                          │
         │                        │                          │
         │                        │ try { ... }              │
         │                        │ catch (Ex1)              │
         │                        ├─────────────────────────>│
         │                        │                          │
         │                        │ Retorna ErrorResponse    │
         │                        │<──────────────────────────
         │<────────────────────────┤                          │
         │ 400 Bad Request         │                          │
         │ { msg, code, time }     │                          │
```

### Tipos de Erro Tratados

| Exceção | HTTP | Causa |
|---|---|---|
| `IllegalArgumentException` | 400 | Dados inválidos |
| `DataIntegrityViolationException` | 409 | Violação de constraint (ex: email duplicado) |
| `JwtException` | 401 | Token inválido/expirado |
| `AccessDeniedException` | 403 | Sem permissão |
| `NoSuchElementException` | 404 | Recurso não encontrado |
| `Exception` (genérico) | 500 | Erro inesperado |

---

## 7. Diagrama de Dependências

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                      │
│  CAMADA PRESENTATION (Controllers)                                  │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ UsuarioController, DesafioController, GameController, ...     │   │
│  └─────────────────┬──────────────────────────────────────────┘   │
│                    │ @Autowired                                    │
│                    ▼                                                │
│  CAMADA APPLICATION (Services)                                    │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ UsuarioService, DesafioService, GamificationService, ...     │   │
│  └─────────────────┬──────────────────────────────────────────┘   │
│                    │ @Autowired                                    │
│                    ▼                                                │
│  CAMADA DATA ACCESS (Repositories)                                │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ UsuarioRepository, DesafioRepository, ...  (JPA interfaces)  │   │
│  └─────────────────┬──────────────────────────────────────────┘   │
│                    │ Spring Data                                   │
│                    ▼                                                │
│  PERSISTÊNCIA (MySQL)                                             │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ Database tables: usuario, desafio, gamificacao, ...          │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  CAMADA LATERAL (Config, Security, Utils)                         │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ SecurityConfig → JwtFiltro → JwtUtil                         │   │
│  │ RedisConfig → GamificationPublisher/Subscriber              │   │
│  │ Web3Config → BlockchainService                              │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Referências

- [Backend CDD Analysis](./backend-cdd-analysis.md)
- [Backend Code Patterns](./backend-code-patterns.md)
- [Backend Services](./backend-service.md)
- [Backend Controllers](./backend-controller.md)

