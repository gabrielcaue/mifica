# 🚀 Guia Rápido: Stubs, Fakes e Mocks

## Resumo Executivo

| Tipo | Usa em | Quando | Velocidade | Exemplo |
|------|--------|--------|-----------|---------|
| **STUB** | Testes Unitários | Retorna valor fixo | ⚡ Rápido | `when(repo.findById(1L)).thenReturn(user)` |
| **FAKE** | Testes de Integração | Implementação real simplificada | ⚡ Rápido | `new FakeUserRepository()` |
| **MOCK** | Testes Unitários | Verifica que foi chamado | ⚡ Rápido | `verify(mockRepo).save(user)` |
| **SPY** | Testes Integração | Mock + chama método real | ⚡ Rápido | `spy(realService)` |
| **Real BD** | Testes E2E | Testar fluxo completo | 🐢 Lento | `@SpringBootTest` + H2 |

---

## 📊 Pirâmide do seu Projeto

```
┌─────────────────────────────────────┐
│  E2E (5-10%)                        │ Docker Compose + MySQL real
│  - Fluxos completos                 │
├─────────────────────────────────────┤
│  Integração (20-30%)                │ @SpringBootTest + H2 em memória
│  - Controller + Service + Repository│
├─────────────────────────────────────┤
│  Unitário (70-80%)                  │ Mocks + Fakes
│  - Services                         │
│  - Utilities                        │
│  - Validações                       │
└─────────────────────────────────────┘
```

---

## 🎯 Estratégia Recomendada Para Seu Projeto

### 1. **GamificationService** → **Testes Unitários (MOCKS)**

Porque: Lógica pura, sem I/O pesado

```java
@Test
void testAddPoints() {
    UserRepository mockRepo = mock(UserRepository.class);
    when(mockRepo.findById(1L)).thenReturn(Optional.of(user));
    
    GamificationService service = new GamificationService(mockRepo);
    service.addPoints(1L, 50);
    
    verify(mockRepo).save(any(User.class)); // ✅ Verifica interação
}
```

### 2. **GamificationController** → **Testes de Integração (FAKE)**

Porque: Precisa testar Controller + Service + Repository juntos

```java
@SpringBootTest
@AutoConfigureMockMvc
void testControllerEndpoint() {
    // MockMvc chama endpoint real
    // Service executa com FakeUserRepository em memória
    mockMvc.perform(post("/api/gamification/add-points"))
        .andExpect(status().isOk());
}
```

### 3. **Redis Pub/Sub** → **Testcontainers (Opcional)**

Porque: Complexo e interdependente

```java
@Testcontainers
class RedisIntegrationTest {
    @Container
    static GenericContainer<?> redis = 
        new GenericContainer<>("redis:latest").withExposedPorts(6379);
    
    // Testa mensageria real
}
```

### 4. **Blockchain (Web3j)** → **MOCK (não real)**

Porque: Caro, externo e desnecessário para unitários

```java
@Test
void testBlockchainCall() {
    Web3j mockWeb3j = mock(Web3j.class);
    // Simula resposta sem chamar rede
    when(mockWeb3j.ethSendTransaction(any())).thenReturn(...);
}
```

---

## 💡 Decisão Rápida: O Que Usar?

```
┌─────────────────────────────────────────────────────┐
│ Preciso testar SERVICE isolado?                     │
│ └─ SIM → Use MOCKS (Mockito)                        │
│ └─ NÃO → Vá para próxima pergunta                   │
│                                                      │
│ Preciso testar CONTROLLER + SERVICE + BD?           │
│ └─ SIM → Use FAKE + @SpringBootTest                 │
│ └─ NÃO → Vá para próxima pergunta                   │
│                                                      │
│ Preciso testar FLUXO COMPLETO (Multi-camada)?       │
│ └─ SIM → Use REAL com Docker Compose (E2E)         │
│ └─ NÃO → Volte uma decisão                          │
│                                                      │
│ Preciso testar COMPONENTE EXTERNO (API, Blockchain)? │
│ └─ SIM → Use MOCK                                   │
│ └─ NÃO → Use STUB ou FAKE                           │
└─────────────────────────────────────────────────────┘
```

---

## 📦 Dependências Mínimas no pom.xml

```xml
<!-- Já tem Spring Boot Test (JUnit 5) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Adicionar: Mockito -->
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

<!-- Opcional: AssertJ (assertions mais legíveis) -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Opcional: Testcontainers (Redis, MySQL real) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>
```

---

## 🔥 Padrões Práticos

### Padrão 1: STUB (Retorna valor)
```java
@Test
void test() {
    UserRepository stub = mock(UserRepository.class);
    when(stub.findById(1L)).thenReturn(Optional.of(user));
    
    // Service usa stub
    assertThat(service.getUser(1L)).isNotNull();
}
```

### Padrão 2: FAKE (Implementação simplificada)
```java
@Test
void test() {
    UserRepository fake = new FakeUserRepository();
    fake.save(user);
    
    // Testa lógica com persistência
    assertThat(fake.findById(user.getId())).isNotEmpty();
}
```

### Padrão 3: MOCK (Verifica chamadas)
```java
@Test
void test() {
    UserRepository mock = mock(UserRepository.class);
    service.updateUser(user);
    
    // Verifica que save foi chamado com user correto
    verify(mock).save(user);
    verify(mock, times(1)).save(any());
}
```

### Padrão 4: SPY (Mock parcial)
```java
@Test
void test() {
    UserRepository real = new UserRepository();
    UserRepository spy = spy(real);
    
    // Chama método real, mas rastreia
    when(spy.findById(1L)).thenReturn(Optional.of(user));
    
    User found = spy.findById(1L);
    verify(spy).findById(1L); // Verifica que foi chamado
}
```

---

## ✅ Checklist para Começar

- [ ] Adicionar Mockito ao `pom.xml`
- [ ] Criar `TestDataFactory` com Builders
- [ ] Criar `FakeUserRepository` e `FakeBadgeRepository`
- [ ] Criar `application-test.yml` com H2
- [ ] Escrever 5-10 testes unitários de GamificationService
- [ ] Escrever 3-5 testes de integração de Controller
- [ ] Configurar cobertura com JaCoCo
- [ ] Adicionar comando `mvn test` no CI/CD

---

## 📞 Próximas Ações

1. **Hoje**: Adicionar Mockito + criar TestDataFactory
2. **Amanhã**: Escrever testes unitários dos Services
3. **Dia 3**: Escrever testes de integração dos Controllers
4. **Dia 4**: Configurar CI/CD para rodar testes
5. **Semana 2**: Adicionar Testcontainers para Redis/MySQL

---

## 🎓 Referências

- 🎯 [Test Pyramid](https://martinfowler.com/bliki/TestPyramid.html) - Martin Fowler
- 📖 [Mockito Best Practices](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- 🚀 [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- 🐋 [Testcontainers](https://www.testcontainers.org/)
