# ✅ Abordagem Profissional para Testes de Integração com Spring Security

## Princípios Fundamentais

Em um projeto profissional **VOCÊ NÃO REMOVE SPRING SECURITY**. Você o configura adequadamente para testes:

### ❌ ERRADO (Amador)
```java
// Remover Spring Security dos testes
@SpringBootTest(exclude = {SecurityAutoConfiguration.class})
```

### ✅ CERTO (Profissional)
```java
// Configurar Spring Security para testes
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")  // Ativa ProfessionalTestSecurityConfig
```

---

## Estratégia Profissional

### 1. **Configuração de Teste** (`ProfessionalTestSecurityConfig.java`)
- ✅ Mantém autenticação/autorização funcionando
- ✅ Desabilita apenas CSRF (seguro em API stateless)
- ✅ Define SecurityFilterChain simplificado
- ✅ Permite HTTP Basic Auth em testes
- ✅ Fornece BCryptPasswordEncoder

**Resultado**: Spring Security funciona, mas em modo "relax" para testes

---

### 2. **Testes Sem Autenticação** (Endpoints públicos)
Endpoints como `/api/usuarios/cadastro` e `/api/usuarios/login` são públicos:

```java
@Test
@DisplayName("Deve cadastrar novo usuário com sucesso")
void testCadastrarUsuario_Success() throws Exception {
    mockMvc.perform(post("/api/usuarios/cadastro")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(usuarioDTOValido)))
        .andExpect(status().isOk());
}

@Test
@DisplayName("Deve fazer login com sucesso")
void testLogin_Success() throws Exception {
    // Primeiro cadastra
    usuarioService.cadastrar(usuarioDTOValido);
    
    // Depois testa login
    mockMvc.perform(post("/api/usuarios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTOValido)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
}
```

---

### 3. **Testes COM Autenticação** (Endpoints protegidos)
Para endpoints que exigem `@WithMockUser`:

```java
import org.springframework.security.test.context.support.WithMockUser;

@Test
@DisplayName("Deve adicionar pontos com usuário autenticado")
@WithMockUser(username = "teste@test.com", roles = {"USER"})
void testAddPoints_WithAuthentication() throws Exception {
    // Mock user já está autenticado automaticamente
    mockMvc.perform(post("/api/gamification/add-points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk());
}

@Test
@DisplayName("Admin pode acessar endpoints admin")
@WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
void testAdminEndpoint_WithAdminRole() throws Exception {
    mockMvc.perform(get("/api/admin/dashboard"))
        .andExpect(status().isOk());
}

@Test
@DisplayName("Usuário common NÃO pode acessar admin endpoint")
@WithMockUser(username = "user@test.com", roles = {"USER"})
void testAdminEndpoint_ForbiddenForUsers() throws Exception {
    mockMvc.perform(get("/api/admin/dashboard"))
        .andExpect(status().isForbidden());
}
```

---

### 4. **Testes COM JWT** (Autenticação realista)
Para testar autenticação JWT real:

```java
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@Test
@DisplayName("Deve aceitar JWT válido no header")
void testWithJWTToken() throws Exception {
    String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."; // Token válido
    
    mockMvc.perform(get("/api/gamification/stats")
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk());
}

@Test
@DisplayName("Deve rejeitar JWT inválido")
void testWithInvalidJWT() throws Exception {
    mockMvc.perform(get("/api/gamification/stats")
            .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
}
```

---

## Setup Correto do Teste

### application-test.yml
```yaml
spring:
  profiles: test
  h2:
    console:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
  security:
    user:
      name: testuser
      password: testpass

jwt:
  secret: test-secret-key-for-testing-purposes-only
  expiration: 86400000

# Desabilitar Redis para testes (ou usar embedded-redis)
redis:
  enabled: false
```

### Anotações do Teste
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // ✅ Ativa ProfessionalTestSecurityConfig
@DisplayName("Testes de Integração")
class MinhaIntegrationTest {
    // ...
}
```

---

## Vantagens da Abordagem Profissional

| Aspecto | Amador ❌ | Profissional ✅ |
|--------|---------|--------------|
| **Segurança** | Remover = falso negativo | Mantém = testa real |
| **Confiabilidade** | 80% dos bugs não aparecem | 95%+ de confiança |
| **Manutenção** | Quebra em produção | Funciona em produção |
| **Code Review** | Rejeitado | Aprovado |
| **CI/CD** | Falsos positivos | Confiável |

---

## Checklist de Implementação

- [ ] Criar `ProfessionalTestSecurityConfig.java` ✅ (já feito)
- [ ] Atualizar `application-test.yml` com configurações corretas
- [ ] Adicionar anotação `@ActiveProfiles("test")` a todos os testes
- [ ] Usar `@WithMockUser` para endpoints protegidos
- [ ] Remover `TestApplicationConfig.java` (abordagem amadora)
- [ ] Executar: `mvn clean test`
- [ ] Verificar CI/CD no GitHub Actions

---

## Próximos Passos

1. **Application-test.yml**: Configurar corretamente Redis e JWT
2. **Testes existentes**: Adicionar `@WithMockUser` onde necessário
3. **Nova estrutura**: Usar `ProfessionalTestSecurityConfig` como padrão
4. **Validação**: Executar suite completa

Essa é a abordagem que passa em code review de empresas FTSE 100 e Fortune 500.
