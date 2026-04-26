# Backend Quick Reference Card

## 🔥 Comandos Essenciais

```bash
# BUILD & RUN
./start-dev.sh                 # Suba tudo (Docker Compose)
cd mifica-backend && mvn clean install  # Build backend
mvn test                       # Rodar testes
mvn test -Dtest=XyzTest      # Um teste específico

# LOGS
docker logs -f mifica-backend    # Logs do backend
curl http://localhost:8080/actuator/health  # Health check

# STOP
docker compose down             # Derruba tudo
```

---

## 🏗️ Stack em 1 Slide

```text
Java 21 + Spring Boot 3.5.6 + Maven
├─ Spring Security (JWT via jjwt 0.11.5)
├─ Spring Data JPA + Hibernate 6 (ORM)
├─ MySQL/PostgreSQL (persistence)
├─ Redis Pub/Sub (async events)
├─ Web3j (blockchain)
├─ Swagger/OpenAPI 2.5.0 (docs)
└─ Actuator (metrics)
```

---

## 📦 Pacotes & Camadas

| Camada | Onde? | Exemplos |
|--------|-------|----------|
| **Controller** | `controller/` | `UsuarioController`, `DesafioController` |
| **Service** | `service/` | `UsuarioService`, `GamificationService` |
| **Repository** | `repository/` | `UsuarioRepository`, `DesafioRepository` |
| **Entity** | `entity/` | `Usuario`, `Desafio`, `Transacao` |
| **DTO** | `dto/` | `UsuarioDTO`, `DesafioCreateDTO` |
| **Config** | `config/` | `SecurityConfig`, `RedisConfig`, `Web3Config` |
| **Filter** | `filter/` | `JwtFiltro` |
| **Util** | `util/` | `JwtUtil`, `EncriptUtil` |
| **Redis** | `redis/` | `GamificationPublisher`, `GamificationSubscriber` |
| **Blockchain** | `blockchain/` | `BlockchainService`, `TransacaoBlockchain` |

---

## 🔐 JWT & Autenticação

### Login
```java
POST /api/usuario/login
{
  "email": "user@example.com",
  "senha": "123456"
}
// Response
{
  "token": "eyJhbGc..."
}
```

### Usar Token
```bash
curl -H "Authorization: Bearer eyJhbGc..." http://localhost:8080/api/usuario/perfil
```

### Validar Token
- Token gerado com duração de **24 horas**
- Valido apenas no `Authorization: Bearer`
- Chave secreta em `JWT_SECRET` (env var)

---

## 🎯 Classes Críticas (ICP > 10)

| Classe | ICP | O que faz | Refatorar? |
|--------|-----|----------|-----------|
| **UsuarioService** | 12-15 | Cadastro + Login + Perfil + Reputação + Créditos | ✅ Sim |
| **UsuarioController** | 11-14 | 8+ endpoints (auth, perfil, admin) | ✅ Sim |
| **GamificationSubscriber** | 8-10 | Redis Pub/Sub para gamificação | 🟡 Depois |
| **BlockchainService** | 6-8 | Transações blockchain | 🟡 Depois |

👉 [Ver análise completa](backend-cdd-analysis.md)

---

## 📝 Criar Novo Endpoint (Passo a Passo)

### 1. DTO (request/response)
```java
// src/main/java/com/mifica/dto/NovaFeatureDTO.java
@Data
@AllArgsConstructor
public class NovaFeatureDTO {
    private String name;
    private String description;
}
```

### 2. Service
```java
// src/main/java/com/mifica/service/NovaFeatureService.java
@Service
public class NovaFeatureService {
    
    @Autowired
    private NovaFeatureRepository repo;
    
    public NovaFeatureDTO criar(NovaFeatureDTO dto) {
        // lógica aqui
        return new NovaFeatureDTO(...);
    }
}
```

### 3. Controller
```java
// src/main/java/com/mifica/controller/NovaFeatureController.java
@RestController
@RequestMapping("/api/nova-feature")
@CrossOrigin("${CORS_ORIGIN:*}")
public class NovaFeatureController {
    
    @Autowired
    private NovaFeatureService service;
    
    @PostMapping
    @Operation(summary = "Criar nova feature")
    public ResponseEntity<NovaFeatureDTO> criar(@RequestBody NovaFeatureDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }
}
```

### 4. Repository
```java
// src/main/java/com/mifica/repository/NovaFeatureRepository.java
public interface NovaFeatureRepository extends JpaRepository<NovaFeature, Long> {
}
```

### 5. Entity
```java
// src/main/java/com/mifica/entity/NovaFeature.java
@Entity
@Table(name = "nova_feature")
@Data
public class NovaFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
}
```

### 6. Teste
```java
// src/test/java/com/mifica/service/NovaFeatureServiceTest.java
@SpringBootTest
public class NovaFeatureServiceTest {
    
    @Autowired
    private NovaFeatureService service;
    
    @Test
    public void testCriar() {
        NovaFeatureDTO dto = new NovaFeatureDTO("Teste", "Descrição");
        NovaFeatureDTO result = service.criar(dto);
        assertNotNull(result.id);
    }
}
```

👉 [Ver templates completos](backend-code-patterns.md)

---

## 🚨 Tratamento de Erros

### BadRequest
```java
throw new IllegalArgumentException("Email inválido");
```

### NotFound
```java
if (!repo.existsById(id)) {
    throw new EntityNotFoundException("Usuário não encontrado");
}
```

### Unauthorized (JWT expirado)
- `JwtFiltro` intercepta e returna `401`
- Frontend redireciona para `/login`

---

## 🎮 Gamificação (Redis Pub/Sub)

### Publicar Evento
```java
@Service
public class GamificationPublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void publicarEventoGamificacao(String userId, int pontos) {
        redisTemplate.convertAndSend("gamification", 
            new GamificationEvent(userId, pontos));
    }
}
```

### Processar Evento
```java
@Component
public class GamificationSubscriber {
    @StreamListener("gamification")
    public void onMessage(GamificationEvent event) {
        // Processar evento e atualizar pontos
    }
}
```

---

## ⛓️ Blockchain (Web3j)

### Registrar Transação
```java
@Service
public class BlockchainService {
    
    public void registrarTransacao(String remetente, String destinatario, double valor) {
        // Validar
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido");
        
        // Registrar em blockchain
        TransacaoBlockchain txn = new TransacaoBlockchain();
        txn.setRemetente(remetente);
        txn.setDestinatario(destinatario);
        txn.setValor(valor);
        txn.setTimestamp(LocalDateTime.now());
        
        repo.save(txn);
    }
}
```

---

## 📊 Métricas & Observabilidade

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# Dashboards Grafana
http://localhost:3000  (admin / admin)
```

---

## 🐛 Debug

### Ver logs do backend
```bash
docker logs -f mifica-backend --tail 100
```

### DB query lenta?
Ativar SQL logs em `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### JWT inválido?
Checar:
1. `JWT_SECRET` env var está setada?
2. Token expirou (24h)?
3. Authorization header está correto: `Authorization: Bearer <token>`?

---

## 📖 Documentação Completa

| Recurso | Link |
|---------|------|
| Onboarding completo | [backend-onboarding.md](backend-onboarding.md) |
| Análise CDD/ICP | [backend-cdd-analysis.md](backend-cdd-analysis.md) |
| Padrões de código | [backend-code-patterns.md](backend-code-patterns.md) |
| Diagramas de fluxo | [backend-workflows.md](backend-workflows.md) |
| Especificações | [docs/packages/00-INDEX.md](../packages/00-INDEX.md) |
| Guia principal | [../../BACKEND_GUIDE.md](../../BACKEND_GUIDE.md) |

---

**Print this card! 📋** Salve em um atalho no seu browser:
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

**Última atualização:** Abril 2026 | **Version:** 1.0.0
