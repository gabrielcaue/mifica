# 🔴 Análise de Falhas de Testes - GitHub Actions

**Data**: 16 de maio de 2026  
**Ambiente**: GitHub Actions (CI/CD)  
**Status**: 37 testes rodados | 19 erros | 0 falhas | 1 skipped

---

## 📊 Resumo dos Problemas

| # | Problema | Testes Afetados | Causa | Severidade |
|---|----------|-----------------|-------|-----------|
| 1 | **ApplicationContext failure threshold** | 16 (Integration tests) | Spring Security não configurado no test profile | 🔴 CRÍTICA |
| 2 | **NullPointerException em GamificationService** | 2 (Fake tests) | userId nulo durante Objects.requireNonNull() | 🔴 CRÍTICA |
| 3 | **Redis Context Load Failure** | GamificationControllerIntegrationTest | ApplicationContext não consegue inicializar | 🟠 ALTA |

---

## 🔍 Problema 1: ApplicationContext Failure (16 testes)

### Classes Afetadas:
- `UsuarioControllerIntegrationTest` — 13 testes falhando
- `GamificationControllerIntegrationTest` — 3 testes falhando

### Erro Detalhado:
```
java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: 
skipping repeated attempt to load context for [WebMergedContextConfiguration@xxx ...]
```

### Causa Raiz:
O Spring Boot tenta carregar o contexto de teste com a configuração:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
```

Mas o `application-test.yml` não desativa completamente os componentes de segurança:
- `org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration`
- `org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration`
- `org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration`

**O problema**: Esses componentes tentam carregar beans que não estão totalmente configurados para o ambiente de teste.

### Solução:

**Arquivo**: `mifica-backend/src/test/resources/application-test.yml`

Adicionar desativação explícita de Security Auto-Configuration:

```yaml
spring:
  profiles:
    active: test
  
  # ✅ Desativar Security Auto-Configuration
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
      - org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration
      - org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration
  
  # ✅ H2 Em Memória
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
    driver-class-name: org.h2.Driver
    username: sa
    password: 

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: false
        dialect: org.hibernate.dialect.H2Dialect

  redis:
    host: localhost
    port: 6379
    timeout: 60000ms

logging:
  level:
    com.mifica: DEBUG
```

---

## 🔍 Problema 2: NullPointerException em GamificationService (2 testes)

### Classes Afetadas:
- `GamificationServiceFakeTest.testCompleteGamificationFlow()` — linha 47
- `GamificationServiceFakeTest.testMultiplePointAdditions()` — linha 69

### Erro Detalhado:
```
java.lang.NullPointerException
  at java.base/java.util.Objects.requireNonNull(Objects.java:233)
  at com.mifica.service.GamificationService.addPoints(GamificationService.java:39)
```

### Causa Raiz:
Em `GamificationService.java:39`:
```java
User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
    .orElseThrow();
```

A variável `userId` é **null**.

Investigação:
1. ✅ `FakeUserRepository.save()` implementa corretamente auto-incremento de ID
2. ✅ O método `save()` retorna a entidade com ID atribuído
3. **❌ PROBLEMA**: Em `TestDataFactory.userBuilder()`, o usuário pode estar sendo criado sem ID inicial

### Verificação do TestDataFactory:

Precisa-se verificar se `TestDataFactory.userBuilder().build()` está retornando um User com ID nulo.

### Solução:

**Arquivo**: `mifica-backend/src/test/java/com/mifica/testhelpers/TestDataFactory.java`

Adicionar verificação no builder ou corrigir o teste:

**Opção 1** - Corrigir o teste (recomendado):
```java
@Test
@DisplayName("Deve persistir pontos e badge corretamente no fluxo completo")
void testCompleteGamificationFlow() {
    // ARRANGE - Cria usuário no fake repository
    User user = TestDataFactory.userBuilder()
        .withName("Alice")
        .withPoints(0)
        .withLevel(1)
        .build();
    
    // ✅ IMPORTANTE: Salvar antes de usar - FakeUserRepository atribui ID
    user = fakeUserRepository.save(user);
    
    // Verificar que ID foi atribuído
    assertThat(user.getId()).isNotNull();
    
    // ACT - Adiciona pontos que atingem o limiar
    gamificationService.addPoints(user.getId(), 100);
    
    // ... resto do teste
}
```

**Opção 2** - Debugar TestDataFactory:

Verificar se `withId()` ou `withoutId()` está sendo chamado:
```java
User user = TestDataFactory.userBuilder()
    .withId(null)  // Explicitamente null para FakeUserRepository atribuir
    .withName("Alice")
    .build();
```

---

## 🔍 Problema 3: Redis Integration Context Load (GamificationRedisIntegrationTest)

### Status:
```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 1
```

Teste skipped = Redis não está disponível no CI/CD.

### Causa:
O CI/CD não tem Redis rodando (via Docker Compose ou serviço).

### Solução:

**Opção 1** - Desativar Redis para testes de integração em CI:

Adicionar ao `application-test.yml`:
```yaml
spring:
  redis:
    enabled: false  # Desativar Redis em testes
```

**Opção 2** - Usar Testcontainers (recomendado para futuro):

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```

---

## ✅ Próximos Passos

### Imediato (para CI passar):
1. ✅ Adicionar `spring.autoconfigure.exclude` ao `application-test.yml`
2. ✅ Corrigir `GamificationServiceFakeTest` garantindo que `user.getId()` nunca seja nulo
3. ✅ Desativar Redis em testes

### Médio Prazo:
- [ ] Adicionar Testcontainers para testes com Redis em CI
- [ ] Adicionar testes de integração com MySQL usando Testcontainers
- [ ] Adicionar coverage mínimo de 70% ao CI

### Longo Prazo:
- [ ] Migrar para Spring Boot 3.x com GraalVM native image
- [ ] Adicionar testes de performance com JMH
- [ ] Configurar SonarQube quality gate automático

---

## 📝 Deploy-Prod.sh Status

**Script**: `/Users/user/mifica/deploy-prod.sh`

### Funcionamento Atual:
✅ Detecta mudanças  
✅ Gera commit automático  
✅ Faz push para GitHub  
⚠️ GitHub Actions tenta rodar testes → **FALHA**  

### O que acontece:
1. Script executa `git add .` + `git commit` + `git push`
2. GitHub Actions dispara workflow
3. Maven tenta: `mvn clean test`
4. 37 testes rodados → **19 erros**
5. Build falha → **Deployment não ocorre**

### Solução:
Corrigir os testes conforme análise acima, então:
```bash
./deploy-prod.sh
```

---

## 🧪 Teste Manual Local

Para verificar antes do push:

```bash
# 1. Rodar testes localmente
cd mifica-backend
mvn clean test -DactiveProfile=test

# 2. Verificar se H2 está sendo usado
mvn clean test -Dspring.profiles.active=test

# 3. Executar apenas testes unitários (sem integration tests)
mvn clean test -Dtest="*UnitTest" -DactiveProfile=test
```

---

## 🔗 Referências

- [Spring Boot Testing Docs](https://spring.io/guides/gs/testing-web/)
- [Spring Security Test](https://spring.io/guides/topical/spring-security-architecture/)
- [Testcontainers](https://testcontainers.com/)
- [GitHub Actions + Spring Boot](https://github.blog/2021-08-10-github-actions-best-practices-for-java-developers/)

