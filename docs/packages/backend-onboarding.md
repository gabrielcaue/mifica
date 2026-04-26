# Backend Onboarding — Guia para Novos Desenvolvedores

Bem-vindo ao backend Mifica! Este guia acelera sua entrada no projeto.

---

## 1. Setup Local (5 minutos)

### Pré-requisitos
- Java 21 (verifique: `java -version`)
- Maven 3.9+ (verifique: `mvn -version`)
- Docker & Docker Compose
- Git

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/gabrielcaue/mifica.git
cd mifica/mifica-backend

# 2. Suba o ambiente local
cd ..
./start-dev.sh

# 3. Aguarde ~1 min até MySQL, Redis, Backend estarem prontos
# 4. Teste a API
curl http://localhost:8080/api/usuarios/teste-swagger

# 5. Acesse Swagger
# Abra em navegador: http://localhost:8080/swagger-ui.html
```

---

## 2. Arquitetura do Projeto (10 minutos)

```
mifica-backend/
├── src/main/java/com/mifica/
│   ├── controller/          # REST endpoints
│   ├── service/             # Lógica de negócio
│   ├── repository/          # Acesso a dados (JPA)
│   ├── entity/              # Domain model (JPA entities)
│   ├── dto/                 # Data Transfer Objects
│   ├── config/              # Configuração (Security, Redis, etc)
│   ├── util/                # Utilidades (JWT, etc)
│   ├── redis/               # Pub/Sub para gamificação
│   ├── blockchain/          # Integração blockchain
│   ├── filter/              # Filtros HTTP customizados
│   └── MificaApplication.java # Main class
│
├── src/main/resources/
│   ├── application.properties # Configuração padrão (dev/local)
│   ├── application-prod.properties # Configuração produção
│   └── db/migration/        # Scripts Flyway (versionados)
│
└── pom.xml                  # Dependências Maven
```

### Fluxo de Requisição

```
Cliente (Frontend/Postman)
    ↓
HTTP Request (GET /api/usuarios/perfil?token=xyz)
    ↓
JwtFiltro (valida token JWT)
    ↓
SecurityConfig (autorização por role)
    ↓
UsuarioController (mapeia endpoint, extrai dados)
    ↓
UsuarioService (executa lógica de negócio)
    ↓
UsuarioRepository (busca no MySQL via JPA)
    ↓
Entity Usuario (carregada do banco)
    ↓
UsuarioService (converte para DTO)
    ↓
UsuarioController (retorna JSON)
    ↓
HTTP Response (200 OK + JSON)
```

---

## 3. Conceitos Fundamentais

### 3.1 SOLID (Princípios de Design)

O projeto aplica **SOLID** para manter código extensível:

| Princípio | Aplicação no Mifica |
|---|---|
| **S**ingle Responsibility | Cada Service tem 1 responsabilidade (ex: `ReputacaoService` só cuida de reputação) |
| **O**pen/Closed | Controllers podem ser estendidos sem modificação (ex: criar novo endpoint em `DesafioController`) |
| **L**iskov Substitution | Repositories seguem contrato de `JpaRepository` |
| **I**nterface Segregation | Services recebem somente as dependências que usam via `@Autowired` |
| **D**ependency Inversion | Todas as dependências são injetadas pelo Spring, não hardcoded |

### 3.2 CDD/ICP (Índice de Complexidade Percebida)

Veja [backend-cdd-analysis.md](backend-cdd-analysis.md) para saber a complexidade de cada classe.

**Regra de ouro:**
- ICP < 3 = Classe simples ✅
- ICP 3-7 = OK, mas monitore 🟡
- ICP > 8 = Candidata a refatoração 🔴

### 3.3 JWT (Autenticação Stateless)

1. Cliente faz POST `/api/usuarios/login` com email + senha
2. Backend valida credenciais com BCrypt
3. Backend gera token JWT assinado
4. Cliente armazena token no localStorage
5. Cliente envia token em cada request: `Authorization: Bearer {token}`
6. JwtFiltro valida assinatura e extrai email do token
7. Request continua autenticado

---

## 4. Primeiros Passos — Fazer Algo Simples

### Tarefa 1: Criar um novo endpoint simples

**Objetivo:** Adicionar endpoint `GET /api/usuarios/teste-new` que retorna uma mensagem.

**Passos:**

1. **Abra `UsuarioController.java`** em `src/main/java/com/mifica/controller/`

2. **Adicione um novo método:**

```java
@GetMapping("/teste-new")
public ResponseEntity<Map<String, String>> testeNovo() {
    Map<String, String> response = new HashMap<>();
    response.put("mensagem", "Novo endpoint criado com sucesso!");
    response.put("timestamp", LocalDateTime.now().toString());
    return ResponseEntity.ok(response);
}
```

3. **Salve o arquivo**

4. **Rode o backend:**
```bash
cd /Users/user/mifica/mifica-backend
mvn spring-boot:run
```

5. **Teste em novo terminal:**
```bash
curl http://localhost:8080/api/usuarios/teste-new
```

✅ Deve retornar:
```json
{
  "mensagem": "Novo endpoint criado com sucesso!",
  "timestamp": "2026-04-26T10:30:45.123456"
}
```

### Tarefa 2: Chamar um Service

**Objetivo:** Criar um endpoint que lista histórico de reputação de um usuário.

1. **Estude `ReputacaoService.java`** — veja método `listarHistorico(String email)`

2. **Adicione endpoint em `UsuarioController`:**

```java
@GetMapping("/reputacao-historico")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> listarReputacaoHistorico(
    @RequestHeader(value = "Authorization", required = false) String token) {
    
    try {
        String email = jwtUtil.extrairEmail(token.replace("Bearer ", ""));
        List<HistoricoReputacaoDTO> historico = reputacaoService.listarHistorico(email);
        return ResponseEntity.ok(historico);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Erro ao buscar histórico: " + e.getMessage());
    }
}
```

3. **Injete `ReputacaoService` no controller:**

```java
@Autowired
private ReputacaoService reputacaoService;
```

4. **Teste com token válido:**
```bash
# 1. Faça login
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@mifica.com","senha":"123456"}'

# 2. Copie o token da resposta

# 3. Use o token para chamar o novo endpoint
curl http://localhost:8080/api/usuarios/reputacao-historico \
  -H "Authorization: Bearer {TOKEN_AQUI}"
```

---

## 5. Arquivos Importantes para Entender

### Leitura Recomendada (em ordem)

1. **[backend-cdd-analysis.md](backend-cdd-analysis.md)** (15 min)
   - Entender complexidade de cada classe
   - Saber quais são críticas

2. **[backend-entity.md](backend-entity.md)** (10 min)
   - Todas as entidades e seus relacionamentos

3. **[backend-service.md](backend-service.md)** (20 min)
   - Cada service e sua responsabilidade
   - Exemplos de uso

4. **[backend-controller.md](backend-controller.md)** (20 min)
   - Todos os endpoints
   - Como autenticar e usar

5. **[backend-code-patterns.md](backend-code-patterns.md)** (15 min)
   - Exemplos de como estender o projeto

### Classes para Estudar

| Classe | Tempo | Por quê |
|---|---|---|
| `MificaApplication.java` | 5 min | Entry point, configuração Spring |
| `SecurityConfig.java` | 10 min | Entender autenticação |
| `JwtUtil.java` | 10 min | Como JWT funciona |
| `UsuarioService.java` | 15 min | Principal service, mas complexa |
| `ReputacaoService.java` | 10 min | Simples, bom exemplo |
| `UsuarioController.java` | 15 min | Principal controller |
| `UsuarioEntity.java` | 10 min | Domain model principal |

---

## 6. Boas Práticas

### ✅ Faça

```java
// 1. Injetar dependências
@Service
public class MinhaService {
    @Autowired
    private OutroService outroService; // ✅ Correto
}

// 2. Usar Optional
Optional<Usuario> usuario = usuarioRepository.findById(id);
usuario.ifPresent(u -> System.out.println(u.getNome())); // ✅ Correto

// 3. Usar variaveis de ambiente
@Value("${jwt.secret}")
private String jwtSecret; // ✅ Correto

// 4. Comentar métodos complexos
// ICP-01: Validação de email com DNS lookup
public boolean validarEmail(String email) { ... } // ✅ Correto
```

### ❌ Não Faça

```java
// 1. Hardcodar valores
private String secret = "meu-secret-aqui"; // ❌ ERRADO

// 2. Criar objetos com new
public class MeuService {
    private OutroService outroService = new OutroService(); // ❌ ERRADO
}

// 3. Ignorar exceções
try {
    // código
} catch (Exception e) {
    // silence is golden — ❌ ERRADO
}

// 4. Métodos muito longos (>50 linhas)
public void fazerTudo() {
    // 200 linhas de código — ❌ ERRADO
    // Quebrar em métodos menores
}
```

---

## 7. Testes Locais

### Rodar Testes Unitários

```bash
cd /Users/user/mifica/mifica-backend
mvn test
```

### Rodar um Teste Específico

```bash
mvn test -Dtest=UsuarioServiceTest
```

### Com Cobertura

```bash
mvn jacoco:report
# Abra target/site/jacoco/index.html
```

---

## 8. Debug (VS Code + Java Extensions)

### Setup Debug

1. Instale extensão `Extension Pack for Java`
2. Abra `MificaApplication.java`
3. Clique em "Debug" acima de `main()`
4. VS Code iniciará debug session

### Colocar Breakpoint

1. Clique no número da linha
2. Ponto vermelho aparece
3. Ao executar, debugger pausa ali

---

## 9. Troubleshooting

### Problema: "Port 8080 already in use"

```bash
# Encontre processo usando porta 8080
lsof -i :8080

# Mate o processo
kill -9 {PID}

# Ou use porta diferente
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Problema: "Connection refused" no MySQL

```bash
# Certifique que containers estão rodando
docker ps

# Se não, execute
cd /Users/user/mifica
./start-dev.sh

# Aguarde ~30s para MySQL estar pronto
```

### Problema: JWT token expirado

- Tokens JWT expiram em **24 horas** por padrão
- Faça novo login para gerar novo token

---

## 10. Próximas Atividades (Incrementais)

### Semana 1
- [ ] Ler documentação até seção 5
- [ ] Rodar testes locais
- [ ] Criar 2-3 endpoints simples

### Semana 2
- [ ] Estudar `ReputacaoService` e `DesafioService`
- [ ] Estender um Service existente
- [ ] Rodar debugger em um endpoint

### Semana 3
- [ ] Revisar [backend-cdd-analysis.md](backend-cdd-analysis.md)
- [ ] Propor refatoração para classe com ICP > 8
- [ ] Abrir PR com mudança

### Semana 4
- [ ] Contribuir com testes unitários
- [ ] Revisar PRs de outros devs

---

## 11. Dúvidas Frequentes

**P: Qual versão Java devo usar?**  
R: Java 21. Spring Boot 3.5.6 suporta 21 nativamente.

**P: Como adicionar nova dependência Maven?**  
R: Edite `pom.xml`, adicione `<dependency>`, rode `mvn clean install`.

**P: Preciso criar um novo banco de dados?**  
R: Não. Use `application.properties` para apontar para MySQL existente. Flyway cria schema automaticamente.

**P: Como estruturar um novo módulo?**  
R: Veja [backend-code-patterns.md](backend-code-patterns.md) para exemplos.

**P: Meu código tem ICP > 8, preciso refatorar?**  
R: Sim. Veja sugestões em [backend-cdd-analysis.md](backend-cdd-analysis.md#padrões-de-refatoração-sugeridos).

---

## 12. Links Rápidos

- 📚 [Documentação Técnica Completa](./00-INDEX.md)
- 🔐 [Security & Authentication](./backend-config.md)
- 📊 [Modelos de Dados](./backend-entity.md)
- 🔄 [Fluxos Críticos](./backend-workflows.md)
- 💻 [Padrões de Código](./backend-code-patterns.md)

---

## Bem-vindo ao Time! 🎉

Qualquer dúvida, abra uma issue ou conversa com o tech lead.

Happy coding! 🚀

