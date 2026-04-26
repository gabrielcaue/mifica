# Backend CDD Analysis — Índice de Complexidade Percebida (ICP)

**Cognitive-Driven Development (CDD)** aplicado ao backend Mifica.

Este documento mapeia a complexidade cognitiva de cada classe, permitindo que novos desenvolvedores identifiquem rapidamente onde concentrar atenção durante manutenção e refatoração.

---

## O que é ICP (Índice de Complexidade Percebida)?

**ICP** mede quantos pontos de decisão uma classe possui:

- **Pontos de decisão**: `if`, `else`, `for`, `while`, `switch`, `case`, `catch`, `ternário (?:)`, `&&`, `||`
- **Delegações**: Chamadas a métodos complexos (p. ex., `Math.max()`); cada delegação = +1
- **Loops aninhados**: Cada nível de aninhamento adiciona pontos

**ICP baixo (<3)**: Classe simples, reta, sem bifurcações.  
**ICP médio (3-7)**: Complexidade moderada, testável.  
**ICP alto (8+)**: Candidata a refatoração, quebra em classes menores.

---

## Levantamento de Complexidade por Camada

### 🔴 CAMADA CRÍTICA (ICP > 10 — Foco de Refatoração)

| Classe | ICP | Linhas | Problema | Recomendação |
|---|---|---|---|---|
| `UsuarioService` | 12-15 | 343 | Acumula cadastro, login, reputação, solicitações de crédito | Quebrar em: `UsuarioCreationService`, `UsuarioProfileService`, `CreditRequestService` |
| `UsuarioController` | 11-14 | 349 | Orquestra 8+ endpoints e múltiplos fluxos | Quebrar em: `AuthController`, `ProfileController`, `AdminUserController` |
| `GamificationSubscriber` | 8-10 | 84 | Parsing de mensagens + buffer + delegação + logging | Extrair parsing em `MessageParser`, buffer em `EventBuffer` |
| `RedisConfig` | 7-9 | 91 | Configuração customizada com fallbacks e listener setup | Documentar bem, deixar como está (setup crítico) |
| `BlockchainService` | 6-8 | 110 | Validações múltiplas, regra de limite do admin, transformação DTO | Extrair validação em `BlockchainValidator` |
| `GamificationService` | 7-9 | 55 | Cálculo de pontos, descoberta de badges, persistência | Dividir em `PointCalculator`, `BadgeDiscovery` |
| `GamificationPublisher` | 6-8 | 50 | Formatação de mensagens + publicação + logging | Extrair em `GamificationMessageFormatter` |

### 🟡 CAMADA MODERADA (ICP 5-7 — Vigilância)

| Classe | ICP | Linhas | Observações |
|---|---|---|---|
| `JwtUtil` | 5-6 | 117 | Geração, extração, validação de token. Bem estruturado. |
| `JwtFiltro` | 4-5 | 105 | Exclusão de rotas + extração + logging. Boa responsabilidade. |
| `SecurityConfig` | 4-5 | 118 | CORS + autorização + filtro. Propósito claro. |
| `ReputacaoService` | 5-6 | 57 | Registra alteração e consulta histórico. Simples, bem organizado. |
| `TransacaoService` | 4-5 | 46 | CRUD de transações. Reto, sem complexidade. |
| `DesafioService` | 4-6 | 42 | Gerencia desafios. Pode ficar simples se separar validações. |
| `ContratoService` | 3-4 | 43 | CRUD de contratos. Bem estruturado. |

### 🟢 CAMADA SIMPLES (ICP 0-3 — OK)

| Classe | ICP | Tipo | Status |
|---|---|---|---|
| `AuthService` | 1-2 | Serviço | Delegação simples. Implementação mínima. |
| `HomeController` | 1 | Controller | Apenas teste. OK. |
| `TransacaoController` | 2-3 | Controller | CRUD REST direto. OK. |
| `ContratoController` | 2-3 | Controller | Endpoints simples. OK. |
| Todos os `*Repository` | 0-1 | Interface | Spring Data JPA. Automático. |
| Todas as `*DTO` | 0-1 | DTO | Só getters/setters. OK. |
| Todas as `*Entity` | 1-3 | Entity | JPA puro. OK. |

---

## Análise Detalhada por Classe

### 1. UsuarioService (ICP: 12-15) 🔴 **CRÍTICA**

**Responsabilidades (MUITO):**
1. Cadastro com validação de email
2. Login com criptografia BCrypt
3. Atualização de perfil
4. Gerenciamento de reputação
5. Gerenciamento de solicitações de crédito
6. Conversão DTO/Entity

**Pontos de decisão:**
- `if (dto.getEmail() != null)` — validação de email
- `if (usuario == null)` — verificação de existência (3x)
- `if (role.toUpperCase().startsWith(...))` — formatação de role
- `if (!passwordEncoder.matches(...))` — validação de senha
- Loops implícitos em `usuarioRepository` queries

**Recomendação de Refatoração:**

```
UsuarioService
├── UsuarioCreationService (cadastro, validação de email)
├── UsuarioProfileService (atualização de perfil)
├── UsuarioAuthService (login, BCrypt)
└── CreditRequestService (solicitações de crédito)
```

---

### 2. UsuarioController (ICP: 11-14) 🔴 **CRÍTICA**

**Responsabilidades (MUITO):**
1. POST /cadastro
2. POST /login
3. GET /perfil (protegido)
4. PUT /perfil
5. POST /criar-admin (senha especial)
6. Operações CRUD administrativas
7. Gamificação (integração GamificationPublisher)

**Pontos de decisão:**
- Validação de email (`.toLowerCase()`, `.trim()`)
- Múltiplos `if` para status HTTP
- Tratamento de `DataIntegrityViolationException`
- Múltiplas bifurcações de role/admin

**Recomendação de Refatoração:**

```
UsuarioController (público)
├── PublicAuthController (cadastro, login — @PostMapping("/auth/..."))
├── UserProfileController (perfil protegido — @GetMapping("/profile/..."))
└── AdminUserController (CRUD admin — @RestController("@PreAuthorize("hasRole('ADMIN')")"))
```

---

### 3. GamificationSubscriber (ICP: 8-10) 🔴 **CRÍTICA**

**Responsabilidades:**
1. Escuta mensagens Redis no canal "gamification-events"
2. Parse manual de formato textual "User:{id} Points:{pontos}"
3. Armazena histórico em buffer circular (máx. 50)
4. Delega ao `GamificationService` para persistência
5. Logging e tratamento de erro

**Pontos de decisão:**
- `try-catch` ao fazer parse
- `while (mensagensRecebidas.size() > MAX_MENSAGENS)` — gestão de buffer
- `split()` e `parseInt()` com possível `NumberFormatException`
- Validação defensiva do formato esperado

**Recomendação de Refatoração:**

```
GamificationSubscriber
├── GamificationMessageParser (extrai userId e points)
├── EventBuffer (gerencia circular buffer)
└── GamificationSubscriber (orquestra)
```

---

### 4. BlockchainService (ICP: 6-8) 🔴 **CRÍTICA**

**Responsabilidades:**
1. Registro de transação blockchain
2. Validação de remetente/destinatário
3. Verificação de regra de limite para admin
4. Cálculo de saldo disponível
5. Transformação DTO ↔ Entity

**Pontos de decisão:**
- `if (dto == null || dto.getDestinatario() == null || dto.getValor() <= 0)` — validações (3x)
- `boolean remetenteEhAdmin = ...` — lógica de role
- `boolean destinatarioEhAdmin = ...` — lógica de role
- `if (!remetenteEhAdmin && destinatarioEhAdmin)` — regra de transferência
- `if (remetenteEhAdmin)` — lógica de limite
- `if (totalAposTransacao > LIMITE_MOVIMENTACAO_ADMIN)` — verificação de limite

**Recomendação de Refatoração:**

```
BlockchainService
├── BlockchainValidator (todas as validações)
├── AdminLimitChecker (lógica de limite)
└── BlockchainService (orquestra)
```

---

### 5. GamificationService (ICP: 7-9) 🔴 **CRÍTICA**

**Responsabilidades:**
1. Adiciona pontos a um usuário
2. Descobre badges desbloqueadas
3. Registra histórico de gamificação
4. Busca badges por tipo

**Pontos de decisão:**
- Múltiplos `if` para validação de usuario
- Loops em `usuario.getBadges()`
- Lógica de descoberta de badge (comparações)
- Registro em histórico

**Recomendação de Refatoração:**

```
GamificationService
├── PointCalculator (adiciona pontos)
├── BadgeDiscovery (detecta badges)
└── GamificationService (orquestra)
```

---

### 6. RedisConfig (ICP: 7-9) 🟡 **CONFIGURAÇÃO CRÍTICA**

**Responsabilidades:**
1. Setup do `RedisMessageListenerContainer`
2. Listener adapter para `GamificationSubscriber`
3. Fallback de configuração (validação)
4. Logging de inicialização

**Observações:**
- Esta classe **deve permanecer com alta complexidade** por ser configuração crítica.
- Bem documentada com comentários explicando cada etapa.
- Recomendação: **Deixar como está**, mas manter documentação atualizada.

---

## Padrões de Refatoração Sugeridos

### Padrão 1: Service Segregation (Single Responsibility)

**Antes:**
```java
@Service
public class UsuarioService {
    public UsuarioDTO criar(UsuarioDTO dto) { ... } // 50 linhas
    public boolean validarLogin(String email, String senha) { ... } // 30 linhas
    public void atualizarPerfil(String email, Usuario dados) { ... } // 25 linhas
    public Usuario criarSolicitacao(BigDecimal valor, ...) { ... } // 40 linhas
}
```

**Depois:**
```java
// 1. Service para criação e validação de usuário
@Service
public class UsuarioCreationService {
    public UsuarioDTO criar(UsuarioDTO dto) { ... }
}

// 2. Service para perfil
@Service
public class UsuarioProfileService {
    public void atualizarPerfil(String email, Usuario dados) { ... }
}

// 3. Service para solicitações de crédito
@Service
public class CreditRequestService {
    public Usuario criarSolicitacao(BigDecimal valor, ...) { ... }
}
```

### Padrão 2: Extract Parser (Estratégia)

**Antes:**
```java
public void onMessage(String message) {
    String[] parts = message.split(" ");
    Long userId = Long.parseLong(parts[0].split(":")[1]);
    int points = Integer.parseInt(parts[1].split(":")[1]);
}
```

**Depois:**
```java
@Component
public class GamificationMessageParser {
    public GamificationEvent parse(String message) {
        String[] parts = message.split(" ");
        Long userId = Long.parseLong(parts[0].split(":")[1]);
        int points = Integer.parseInt(parts[1].split(":")[1]);
        return new GamificationEvent(userId, points);
    }
}

public void onMessage(String message) {
    GamificationEvent event = parser.parse(message);
    gamificationService.addPoints(event.getUserId(), event.getPoints());
}
```

### Padrão 3: Validator Pattern (Validação Centralizada)

**Antes:**
```java
public TransacaoBlockchainDTO registrarTransacao(...) {
    if (dto == null) throw ...
    if (dto.getDestinatario() == null) throw ...
    if (dto.getValor() <= 0) throw ...
    if (!remetenteEhAdmin && destinatarioEhAdmin) throw ...
}
```

**Depois:**
```java
@Component
public class TransacaoValidator {
    public void validar(TransacaoBlockchainDTO dto, Usuario remetente, Usuario destinatario) {
        if (dto == null) throw ...
        if (dto.getDestinatario() == null) throw ...
        if (dto.getValor() <= 0) throw ...
        validarTransferencia(remetente, destinatario);
    }
}

public TransacaoBlockchainDTO registrarTransacao(...) {
    validator.validar(dto, remetente, destinatario);
    // ... continua
}
```

---

## Roadmap de Refatoração

### Fase 1 (Semana 1) — Services Críticos
- [ ] Quebrar `UsuarioService` em 4 services menores
- [ ] Criar `UsuarioCreationService`, `UsuarioProfileService`, `UsuarioAuthService`
- [ ] Adicionar testes unitários para cada service

### Fase 2 (Semana 2) — Controllers
- [ ] Quebrar `UsuarioController` em 3 controllers
- [ ] Criar `PublicAuthController`, `UserProfileController`, `AdminUserController`
- [ ] Atualizar rotas no `docker-compose` se necessário

### Fase 3 (Semana 3) — Redis & Blockchain
- [ ] Extrair parser de `GamificationSubscriber`
- [ ] Extrair validador de `BlockchainService`
- [ ] Adicionar testes de integração

### Fase 4 (Semana 4) — Documentação
- [ ] Atualizar `backend-code-patterns.md` com exemplos novos
- [ ] Criar guia de onboarding para novos devs
- [ ] Revisar toda documentação

---

## Checklist para Novos Desenvolvedores

### Antes de Fazer Mudanças

- [ ] Leia este documento até o fim
- [ ] Consulte a ICP da classe que vai modificar
- [ ] Se ICP > 8, converse com o tech lead sobre refatoração
- [ ] Rode os testes locais: `mvn test`
- [ ] Verifique se sua mudança aumenta a ICP

### Ao Adicionar Nova Funcionalidade

- [ ] Crie uma nova classe se ICP prevista > 6
- [ ] Use injeção de dependência (`@Autowired`)
- [ ] Siga padrão de nomeação (Service, Controller, Repository)
- [ ] Documente métodos complexos com comentários `// ICP-XX`
- [ ] Escreva testes unitários

### Ao Revisar PRs

- [ ] Verifique ICP da classe modificada
- [ ] Valide se segue padrões SOLID
- [ ] Confirme testes estão passando
- [ ] Comente sobre oportunidades de simplificação

---

## Referências

- **CDD Blog Zup**: https://zup.com.br/blog/cognitive-driven-development-cdd/
- **Clean Code (Robert Martin)**: Capítulo 3 — Functions
- **Refactoring (Martin Fowler)**: Extract Method, Extract Class

