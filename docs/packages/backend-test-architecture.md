# SDD: Arquitetura de Testes - `com.mifica.test`

## Objetivo
Estabelecer a estratégia de testes e mocking para garantir que toda a plataforma Mifica funcione sob cobertura de testes isolados (unit) e integrados (integration) sem dependência de ambiente externo.

## Escopo

### Inclui
- ✅ **Test Doubles:** Fake Repositories em memória (FakeUserRepository, FakeBadgeRepository)
- ✅ **Builder Pattern:** TestDataFactory para criação de dados de teste reutilizáveis
- ✅ **In-Memory Database:** H2 para testes de persistência isolados
- ✅ **Unit Tests:** JUnit 5 com Mockito para serviços
- ✅ **Integration Tests:** Spring Boot Test com MockMvc para endpoints
- ✅ **Reflection-based ID Assignment:** Resolução de constraints de tipos genéricos em testes
- ✅ **Lazy vs Eager Loading:** Simulação de comportamentos JPA no contexto de testes

### Não Inclui
- ❌ Testes end-to-end com Redis real (usar Testcontainers em futuro upgrade)
- ❌ Testes com blockchain real (usar Web3j mock)
- ❌ Testes contra MySQL production (usar H2 in-memory)

---

## Contratos e Interfaces

### 1. **Fake Repository Pattern**
```java
public interface FakeRepository<T, ID> {
    <S extends T> S save(S entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void reset(); // Limpa state para isolamento entre testes
}
```

**Implementações:**
- `FakeUserRepository` → `UserRepository` com HashMap<Long, User>
- `FakeBadgeRepository` → `BadgeRepository` com HashMap<Long, Badge>

### 2. **Test Data Factory Builder**
```java
public class TestDataFactory {
    public static UserBuilder user() { ... }
    public static BadgeBuilder badge() { ... }
    public static TransacaoBuilder transacao() { ... }
    // Retornam builders fluentes para criar objetos com estado padrão
}
```

### 3. **Reflection-based ID Assignment**
Para suportar tipos genéricos que não expõem setters públicos:
```java
if (entity.getId() == null) {
    try {
        entity.getClass().getDeclaredMethod("setId", Long.class)
            .invoke(entity, idCounter++);
    } catch (Exception e) {
        // Entity vem com ID pré-definido
    }
}
```

---

## Regras e Invariantes

| Regra | Descrição |
|-------|-----------|
| **Isolamento** | Cada teste deve rodar independente; uso de `@BeforeEach` para reset de state |
| **Determinismo** | IDs em Fake Repositories começam em 1 e incrementam sequencialmente |
| **Sem I/O Real** | Testes unitários não devem fazer conexões HTTP, DB ou Redis |
| **Eager vs Lazy** | `getById()` retorna entidade; `getReferenceById()` retorna proxy (mesmo comportamento em fake) |
| **Cleanup** | Método `reset()` em cada Fake Repository limpa HashMap entre testes |

---

## Critérios de Aceitação (Testáveis)

- [ ] Todo serviço tem testes unitários com Mockito
- [ ] Todo controller tem testes de integração com MockMvc
- [ ] Cobertura mínima de 70% em linhas críticas (service, repository, entity)
- [ ] Todos os testes passam localmente (`mvn clean test`)
- [ ] Todos os testes passam em CI/CD (GitHub Actions)
- [ ] Testes unitários executam em < 2s
- [ ] Testes de integração executam em < 10s
- [ ] Fake Repositories suportam todos os métodos abstratos de JpaRepository
- [ ] TestDataFactory cria objetos com estado válido por padrão

---

## Dependências e Integrações

### Dependências de Teste (pom.xml)
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Integrações com Camadas Principais
```
┌─ Testes Unitários (Service)
│  └─ Mockito mock de Repositories
│  └─ AssertJ para assertions
│
├─ Testes de Integração (Controller)
│  └─ Spring Boot Test Context
│  └─ MockMvc para requisições HTTP
│  └─ H2 Database para persistência
│
└─ Testes de Aceitação (E2E - futuro)
   └─ Testcontainers para Redis/MySQL
   └─ Web3j mock para Blockchain
```

---

## Riscos e Trade-offs

| Risco | Mitigação |
|-------|-----------|
| **Fake Repositories divergem da realidade** | Revisar periódicamente contra comportamentos JPA; adicionar testes de integração com H2 |
| **Reflection brittle para tipos genéricos** | Wrappear em try-catch; logging de falhas; considerar alternativas com constructores parametrizados |
| **Testes lento em CI/CD** | Paralelizar testes via Maven Surefire; cache de dependências |
| **Cobertura superficial** | Usar Jacoco para medir coverage; revisar em cada PR; enforcer > 70% em sonarqube |
| **State leaking entre testes** | Garantir `@BeforeEach reset()` em todas as suites; uso de `@Transactional` para H2 rollback |

---

## Exemplos de Uso

### Unit Test com Mockito
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    @Test
    void deveListarUsuarios() {
        // Arrange
        User user = TestDataFactory.user().withName("João").build();
        when(usuarioRepository.findAll()).thenReturn(List.of(user));
        
        // Act
        List<User> result = usuarioService.listarTodos();
        
        // Assert
        assertThat(result).hasSize(1).contains(user);
    }
}
```

### Integration Test com H2
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UsuarioControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Test
    void deveRetornarUsuarios() {
        // Arrange
        User user = usuarioRepository.save(TestDataFactory.user().build());
        
        // Act
        ResponseEntity<User[]> response = restTemplate.getForEntity(
            "/api/usuarios", User[].class);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(user);
    }
}
```

### Fake Repository em Teste
```java
@Test
void deveArmazenarUsuarioEmMemoria() {
    // Arrange
    FakeUserRepository fakeRepo = new FakeUserRepository();
    User user = TestDataFactory.user().build();
    
    // Act
    User saved = fakeRepo.save(user);
    
    // Assert
    assertThat(saved.getId()).isNotNull();
    assertThat(fakeRepo.findById(saved.getId())).isPresent();
}
```

---

## Checklist de Implementação

- [x] JUnit 5 configurado e funcional
- [x] Mockito integrado para testes unitários
- [x] AssertJ para assertions fluentes
- [x] TestDataFactory com builders padrão
- [x] FakeUserRepository implementado
- [x] FakeBadgeRepository implementado
- [x] Reflection-based ID assignment functional
- [x] H2 configurado para testes de integração
- [x] MockMvc para testes de controller
- [x] GitHub Actions executando testes em CI/CD

---

## Referências

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- `backend-onboarding.md` - Setup e primeiros testes
- `backend-quick-reference.md` - Atalhos e cheat sheet

