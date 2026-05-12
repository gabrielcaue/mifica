# Estratégia de Testes - Projeto Mifica

## 🎯 Contexto do Projeto

O projeto Mifica é uma **aplicação full-stack complexa** com:
- **Backend**: Spring Boot 3.5 + Java 21 com JPA/Hibernate
- **Banco de dados**: MySQL (primário) + H2 (testes) + Redis (Pub/Sub)
- **Blockchain**: Web3j para integração smart contracts
- **Segurança**: Spring Security + JWT
- **Gamificação**: Pub/Sub com Redis + pontos + badges
- **Camadas**: Controllers → Services → Repositories → Entities

### ⚠️ Problema Identificado
O projeto está **muito acoplado à produção**, causando:
- Testes lentos (dependem de banco real)
- Testes frágeis (quebram com mudanças estruturais)
- Difícil de paralelizar
- Dificuldade de simular cenários de erro

---

## 📊 Pirâmide de Testes Recomendada

```
        /\
       /  \         E2E / Testes de Sistema
      /    \        (5-10% do tempo)
     /______\       
    /        \      Testes de Integração
   /          \     (20-30% do tempo)
  /____________\    
 /              \   Testes Unitários
/______________\   (70-80% do tempo)
```

---

## 🧪 Estratégia por Tipo de Teste

### 1️⃣ TESTES UNITÁRIOS (70-80% - O mais importante!)

**O que testar:**
- Lógica de negócio isolada
- Services e Utilities
- Cálculos e validações
- Regras de gamificação

**O que usar:**
- ✅ **Mocks** (substitui dependências externas)
- ✅ **Stubs** (retorna dados pré-definidos)
- ✅ **Fakes** (implementação simplificada)

**Ferramentas:**
```xml
<!-- Mockito para mocks -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

**Exemplo - Teste Unitário:**
```java
@Test
void testAddPoints_ReachesLevel2() {
    // ARRANGE - Setup com Mocks
    User user = new User();
    user.setId(1L);
    user.setPoints(50);
    user.setLevel(1);
    
    UserRepository mockUserRepo = mock(UserRepository.class);
    BadgeRepository mockBadgeRepo = mock(BadgeRepository.class);
    
    when(mockUserRepo.findById(1L)).thenReturn(Optional.of(user));
    when(mockUserRepo.save(any(User.class))).thenReturn(user);
    
    GamificationService service = new GamificationService(mockUserRepo, mockBadgeRepo);
    
    // ACT
    service.addPoints(1L, 50);
    
    // ASSERT
    assertEquals(100, user.getPoints());
    assertEquals(2, user.getLevel());
    verify(mockBadgeRepo).save(any(Badge.class)); // Verifica badge criada
}
```

---

### 2️⃣ TESTES DE INTEGRAÇÃO (20-30%)

**O que testar:**
- Interação entre camadas (Controller ↔ Service ↔ Repository)
- Persistência no banco (com H2 em memória)
- Transações e rollback
- Redis Pub/Sub
- Security (autenticação)

**O que usar:**
- ✅ **@SpringBootTest** com perfil `test`
- ✅ **TestRestTemplate** para chamar endpoints
- ✅ **H2 em memória** (não toca MySQL)
- ✅ **Mock de componentes externos** (Blockchain, Email)

**Ferramentas adicionais:**
```xml
<!-- Testcontainers para ambientes isolados -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>
```

**Exemplo - Teste de Integração:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GamificationIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testEndToEndGamification() {
        // ARRANGE - cria usuário real no H2
        User user = new User();
        user.setName("Test User");
        user.setPoints(0);
        user.setLevel(1);
        user = userRepository.save(user);
        
        // ACT - chama endpoint real
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/api/gamification/add-points",
            new AddPointsRequest(user.getId(), 50),
            User.class
        );
        
        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody().getPoints());
    }
}
```

---

### 3️⃣ TESTES DE SISTEMA / E2E (5-10%)

**O que testar:**
- Fluxos completos (login → criar contrato → ganhar pontos)
- UI + API + Banco + Redis tudo junto
- Validações de negócio end-to-end
- Segurança (JWT válido/inválido)

**O que usar:**
- ✅ **Docker Compose** para orquestração (MySQL real + Redis real)
- ✅ **Testcontainers** para ambiente descartável
- ✅ **RestAssured** para testes API
- ✅ **Selenium/Cypress** para teste UI (se houver)

**Exemplo - Teste de Sistema:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FullSystemTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("mifica_test")
        .withUsername("root")
        .withPassword("test");
    
    @Test
    void testCompleteUserJourney() {
        // Usuário se registra
        // Usuário faz login
        // Usuário cria contrato
        // Usuário completa desafio
        // Usuário ganha pontos
        // Usuário sobe de nível
        // Sistema emite evento Redis
        // Badge é concedida
    }
}
```

---

## 🎭 Padrões: Stubs vs Fakes vs Mocks

| Padrão | Uso | Exemplo |
|--------|-----|---------|
| **STUB** | Retorna valores pré-definidos, sem lógica | `when(repo.findById(1L)).thenReturn(user)` |
| **FAKE** | Implementação real simplificada | `FakeUserRepository extends UserRepository` |
| **MOCK** | Verifica que foi chamado corretamente | `verify(mockRepo).save(user)` |
| **SPY** | Mock parcial (chama método real + rastreia) | `spy(realService)` |

### Quando usar cada um:

```java
// ❌ NÃO FAZER - teste frágil, acoplado ao BD
@Test
void testBadUserCriteria() {
    userService.create(new User()); // Cria registro REAL no MySQL
}

// ✅ FAZER - teste rápido com STUB
@Test
void testBadUserCriteria() {
    UserRepository stubRepo = mock(UserRepository.class);
    when(stubRepo.findAll()).thenReturn(Collections.emptyList());
    
    UserService service = new UserService(stubRepo);
    List<User> result = service.searchBadUsers();
    assertTrue(result.isEmpty());
}

// ✅ FAZER - teste de integração com FAKE
@Test
void testCompleteUserFlow() {
    UserRepository fakeRepo = new FakeUserRepository();
    fakeRepo.save(new User("John"));
    
    UserService service = new UserService(fakeRepo);
    User found = service.findByEmail("john@example.com");
    assertNotNull(found);
}
```

---

## 📋 Recomendação Final

Para seu projeto **Mifica**, a estratégia ideal é:

```
✅ TESTES UNITÁRIOS (70-80%)
   → Todos Services com Mocks do Repository
   → Lógica de gamificação com Fakes
   → Validações com Stubs

✅ TESTES DE INTEGRAÇÃO (20-30%)
   → Controllers com H2 em memória
   → Services com Repositories reais (H2)
   → Redis com Testcontainers (opcional)

✅ TESTES E2E (5-10%)
   → Docker Compose com MySQL real + Redis real
   → Apenas happy paths críticos
   → Executar em CI/CD (não local)
```

---

## 🛠️ Próximos Passos

1. **Adicionar Mockito ao pom.xml**
2. **Criar helpers de teste** (Builders, TestDataFactory)
3. **Refatorar Services para serem testáveis** (injetar dependências)
4. **Criar testes unitários para cada Service**
5. **Criar testes de integração para Controllers principais**
6. **Configurar perfil `test` no application-test.yml**
7. **Integrar ao CI/CD** (executar testes antes de deploy)

---

## 📚 Referências

- [Test Pyramid - Martin Fowler](https://martinfowler.com/bliki/TestPyramid.html)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Testcontainers](https://www.testcontainers.org/)
