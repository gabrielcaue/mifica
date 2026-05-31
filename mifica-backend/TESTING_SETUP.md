# Configuração de Testes

## Application-test.yml

Use este arquivo para configurar o ambiente de testes:

```yaml
spring:
  profiles:
    active: test
  
  # ✅ H2 Em Memória (não toca MySQL)
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
    driver-class-name: org.h2.Driver
    username: sa
    password: 

  # ✅ Cria tabelas automaticamente para testes
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: false

  # ✅ Redis desabilitado em testes (use Testcontainers se precisar)
  redis:
    host: localhost
    port: 6379
    # Ou use MOCK do Testcontainers

# ✅ Logs
logging:
  level:
    com.mifica: DEBUG
    org.springframework.test: DEBUG
    org.hibernate.SQL: DEBUG
```

## Estrutura de Diretórios para Testes

```
mifica-backend/
├── src/
│   ├── main/java/com/mifica/
│   │   ├── entity/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── repository/
│   │   └── ...
│   │
│   └── test/java/com/mifica/
│       ├── testhelpers/          ← Factory + Builders
│       │   └── TestDataFactory.java
│       │
│       ├── repository/           ← Fakes para repositórios
│       │   ├── FakeUserRepository.java
│       │   ├── FakeBadgeRepository.java
│       │   └── ...
│       │
│       ├── service/              ← Testes de Service
│       │   ├── GamificationServiceUnitTest.java
│       │   ├── GamificationServiceFakeTest.java
│       │   ├── UsuarioServiceUnitTest.java
│       │   └── ...
│       │
│       ├── controller/           ← Testes de Integração
│       │   ├── GamificationControllerIntegrationTest.java
│       │   ├── UsuarioControllerIntegrationTest.java
│       │   └── ...
│       │
│       └── e2e/                  ← Testes End-to-End (opcional)
│           ├── CompleteUserJourneyE2ETest.java
│           └── ...
```

## Boas Práticas

### ✅ DO (Faça)

```java
// ✅ Use nomes descritivos
@Test
@DisplayName("Deve adicionar pontos e criar badge ao atingir 100 pontos")
void testAddPoints_ReachesLevel2() { }

// ✅ Use @DisplayName
@DisplayName("GamificationService - Testes Unitários")
class GamificationServiceUnitTest { }

// ✅ Use Arrange-Act-Assert
@Test
void test() {
    // ARRANGE - Setup
    User user = TestDataFactory.userBuilder().build();
    
    // ACT - Executa
    service.addPoints(user.getId(), 50);
    
    // ASSERT - Verifica
    assertEquals(50, user.getPoints());
}

// ✅ Use Builders para dados de teste
User user = TestDataFactory.userBuilder()
    .withPoints(50)
    .withLevel(1)
    .build();

// ✅ Isole testes com @BeforeEach
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new GamificationService(mockRepo);
}

// ✅ Use AssertJ para assertions mais legíveis
assertThat(user.getPoints()).isEqualTo(100);
assertThat(badgeRepository.count()).isGreaterThan(0);
```

### ❌ DON'T (Não faça)

```java
// ❌ Não use @SpringBootTest para testes unitários
@SpringBootTest
class GamificationServiceUnitTest { } // ERRADO

// ❌ Não teste banco de dados real (MySQL) em testes unitários
userRepository.save(user); // Acoplado!

// ❌ Não use números mágicos
assertEquals(100, user.getPoints()); // O que significa 100?

// ❌ Não teste múltiplas coisas em um teste
@Test
void testEverything() {
    // Cria usuário
    // Adiciona pontos
    // Cria badge
    // Busca em Redis
    // Envia email
    // ❌ MUITO!
}

// ❌ Não reutilize dados entre testes
private static User sharedUser; // ❌ Testes ficam interdependentes

// ❌ Não teste a biblioteca do Spring (já foi testado!)
@Test
void testSpringWiredBean() {
    assertNotNull(userRepository); // ❌ Óbvio
}
```

## Convenções de Nomenclatura

```
[Classe]Test[Estilo]
└─ GamificationServiceUnitTest (teste unitário com mocks)
└─ GamificationServiceFakeTest (teste com fake repository)
└─ GamificationControllerIntegrationTest (teste de integração)
└─ CompleteUserJourneyE2ETest (teste end-to-end)

[Método]
└─ test[O que está sendo testado]_[Cenário]_[Resultado esperado]
└─ testAddPoints_ReachesLevel2AndCreatesBadge
└─ testAddPoints_UserNotFound_ThrowsException
```

## Cobertura de Testes

Recomendado: **80%+ para Services e Controllers**

```bash
# Executar com cobertura (usando JaCoCo)
mvn clean test jacoco:report

# Relatório em: target/site/jacoco/index.html
```

## Executar Testes

```bash
# Todos os testes
mvn clean test

# Apenas testes unitários
mvn test -Dtest=*Unit*

# Apenas testes de integração
mvn test -Dtest=*Integration*

# Teste específico
mvn test -Dtest=GamificationServiceUnitTest

# Com output
mvn test -X
```

## Sugestão: Adicionar ao CI/CD

```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn clean test
      - run: mvn jacoco:report
      - uses: codecov/codecov-action@v2
```
