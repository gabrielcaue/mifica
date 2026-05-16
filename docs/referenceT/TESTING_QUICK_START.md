# 🧪 Testes no Mifica — Guia Rápido

## Resumo Executivo

O projeto Mifica usa uma estratégia de testes **em camadas** com isolamento total (sem dependências externas em testes unitários):

| Tipo | Ferramenta | O que testa |
|------|-----------|-----------|
| **Unit** | JUnit 5 + Mockito | Lógica de negócio (Services) |
| **Integration** | Spring Test + H2 | Controllers + Persistência |
| **Fake** | FakeUserRepository | Sem banco real, apenas memória |
| **Coverage** | Jacoco | % de linhas cobertas |

---

## 1️⃣ Rodando Testes Localmente

```bash
# Todos os testes
cd /Users/user/mifica/mifica-backend
mvn clean test

# Apenas unit tests
mvn test -Dtest=**Service*

# Apenas integration tests  
mvn test -Dtest=**IntegrationTest

# Com relatório de cobertura
mvn clean test jacoco:report
# Resultado em: target/site/jacoco/index.html
```

---

## 2️⃣ Arquitetura de Testes

### Unit Tests (Serviços)

Testa **lógica de negócio** com mocks de dependências:

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

### Integration Tests (Controllers)

Testa **fluxo HTTP completo** com H2 in-memory:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UsuarioControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Test
    void deveRetornarListaDeUsuarios() {
        // Arrange
        User user = usuarioRepository.save(
            TestDataFactory.user().withName("Maria").build()
        );
        
        // Act
        ResponseEntity<User[]> response = restTemplate.getForEntity(
            "/api/usuarios", User[].class
        );
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(user);
    }
}
```

### Fake Repositories

Implementação em **memória** de `JpaRepository` para testes sem banco:

```java
FakeUserRepository fakeRepo = new FakeUserRepository();

// Save
User user = TestDataFactory.user().build();
User saved = fakeRepo.save(user);
assertThat(saved.getId()).isNotNull();

// Find
Optional<User> found = fakeRepo.findById(saved.getId());
assertThat(found).isPresent();

// Delete
fakeRepo.delete(saved);
assertThat(fakeRepo.findById(saved.getId())).isEmpty();

// Reset entre testes
fakeRepo.reset();
```

---

## 3️⃣ Test Data Factory

**Builder Pattern** para criar objetos de teste com estado padrão:

```java
// User
User user = TestDataFactory.user()
    .withName("João Silva")
    .withEmail("joao@example.com")
    .withRole(Role.ROLE_USER)
    .build();

// Badge
Badge badge = TestDataFactory.badge()
    .withTitle("Herói")
    .withUser(user)
    .build();

// Transação
Transacao transacao = TestDataFactory.transacao()
    .withUser(user)
    .withAmount(1000.0)
    .build();
```

Vantagens:
- ✅ Reutilizável
- ✅ Legível
- ✅ Estado válido por padrão
- ✅ Fácil customização

---

## 4️⃣ Tecnologias Usadas

| Dependência | Versão | Função |
|---|---|---|
| `junit-jupiter` | 5.x | Framework principal |
| `mockito-core` | 4.x+ | Mock de dependências |
| `assertj-core` | 3.x+ | Assertions fluentes |
| `spring-boot-starter-test` | 3.5.6 | Spring Test + MockMvc |
| `h2` | 2.x | Banco em memória |
| `jacoco-maven-plugin` | 0.8.x | Cobertura |

---

## 5️⃣ Estratégia: Reflexão para Tipos Genéricos

Entidades JPA não expõem `setId()` público (é gerado pelo `@GeneratedValue`).  
Para suportar tipos genéricos em Fake Repositories:

```java
// Em FakeUserRepository.save(S entity)
if (entity.getId() == null) {
    try {
        // Reflection: chama setId() via método privado
        entity.getClass()
            .getDeclaredMethod("setId", Long.class)
            .invoke(entity, idCounter++);
    } catch (Exception e) {
        // Se falhar, entity já vem com ID pré-definido
    }
}
```

**Por que isso é necessário:**
- `S extends User` não garante que `setId()` existe
- Reflection permite "forçar" a chamada ao método privado
- Try-catch torna robusto contra entidades sem o método

---

## 6️⃣ CI/CD — GitHub Actions

Testes rodam automaticamente a cada push:

```yaml
# .github/workflows/test.yml
name: Run Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: mvn clean test
```

---

## 7️⃣ Checklist: Antes de Mergear

- [ ] Todos os testes passam localmente (`mvn clean test`)
- [ ] Cobertura > 70% em classes críticas (`target/site/jacoco/`)
- [ ] Nenhum warning no SonarQube
- [ ] Testes de controller adicionados para novo endpoint
- [ ] TestDataFactory atualizado para novas entidades
- [ ] GitHub Actions passou (check mark no PR)

---

## 8️⃣ Problemas Comuns

### ❌ "LazyInitializationException"
```
Acontece quando:
- Acessa propriedade lazy-loaded de entidade fora da transação

Solução:
- Use @Transactional em testes de integração
- Ou eager-load a entidade com getById()
```

### ❌ "Test timed out"
```
Acontece quando:
- Teste preso em loop infinito ou operação bloqueante

Solução:
- Adicione @Timeout(1) ou @Timeout(seconds = 5) ao método
- Verifique mocks estão configurados corretamente
```

### ❌ "No qualifying bean of type 'X' found"
```
Acontece quando:
- Spring não consegue injetar dependência em teste

Solução:
- Adicione @Mock ou @MockBean para a dependência
- Ou configure @TestConfiguration com @Bean
```

---

## 9️⃣ Dicas de Performance

```bash
# Paralelizar testes (4 threads)
mvn -T 4 test

# Apenas testes novos (após commit recente)
mvn test -Dtest=NewTestClass

# Pular compilação se código não mudou
mvn test -pl :backend -am
```

---

## 🔟 Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Tutorial](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [SDD detalhado](backend-test-architecture.md) — Para padrões avançados
- [Backend Quick Reference](docs/packages/backend-quick-reference.md) — Cheat sheet

---

**Última atualização:** 16 de maio de 2026  
**Mantém-se sincronizado com:** Maven pom.xml, CI/CD pipeline
