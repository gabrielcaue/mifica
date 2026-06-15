# Stack Tecnológica Mifica — Mapa Visual (2026)

## 🏗️ Arquitetura de Alto Nível

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Frontend)                             │
│                                                                       │
│  ┌─────────────────┐  ┌──────────────────┐  ┌─────────────────┐   │
│  │ React 18        │  │ Flutter + Dart   │  │ Streamlit Panel │   │
│  │ + Vite 7        │  │ (Mobile)         │  │ (Analytics)     │   │
│  │ + Tailwind      │  │ + Secure Storage │  │ + Python        │   │
│  └────────┬────────┘  └────────┬─────────┘  └────────┬────────┘   │
│           │                    │                     │               │
│           └────────────────────┴─────────────────────┘               │
│                                │                                     │
│                          HTTP/S + JWT                                │
└────────────────────────────────┼─────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    BACKEND (API - Núcleo)                            │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Spring Boot 3.5.6 + Java 17 (LTS)                            │   │
│  │                                                               │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │   │
│  │  │ Controllers  │  │  Services    │  │ Repositories │      │   │
│  │  │ (REST)       │  │  (Business)  │  │  (Persist)   │      │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘      │   │
│  │         │                │                   │               │   │
│  │  Spring Security  Redis Pub/Sub          JPA/Hibernate      │   │
│  │    + JWT Filtro    + Listener           + Envers (Audit)    │   │
│  │                                                               │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │   │
│  │  │  Web3j 4.9.8 │  │ Validator +  │  │  Flyway      │      │   │
│  │  │  (Blockchain)│  │  EL          │  │  (Migrations)│      │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘      │   │
│  └─────────────────────────────────────────────────────────────┘   │
└────────────┬─────────────────┬──────────────────────┬────────────────┘
             │                 │                      │
             ▼                 ▼                      ▼
      ┌────────────┐   ┌──────────────┐    ┌──────────────────┐
      │   Redis    │   │   MySQL 8.0  │    │   Blockchain     │
      │  Upstash   │   │   (Prod)      │    │   Network        │
      │(Pub/Sub)   │   │   H2 (Tests)  │    │   (Web3j)        │
      └────────────┘   └──────────────┘    └──────────────────┘
```

---

## 📊 Camadas e Responsabilidades

```
┌─────────────────────────────────────────────────────────────┐
│ TESTE (JUnit 5 + Mockito + AssertJ)                         │
│ ├─ Unit Tests: Serviços com mocks                           │
│ ├─ Integration Tests: Controllers + H2 in-memory            │
│ ├─ Fake Repositories: HashMap<ID, Entity> emulação          │
│ └─ Test Data Factory: Builders para objetos de teste        │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│ APLICAÇÃO (Spring Boot)                                     │
│ ├─ REST Controllers (OpenAPI/Swagger 2.5.0)                 │
│ ├─ Business Services (Gamificação, Reputação, Blockchain)   │
│ ├─ JPA Repositories (Spring Data + Hibernate 6.6.29)        │
│ ├─ Entities com Auditoria (Envers @Audited)                │
│ ├─ JWT Auth (jjwt 0.11.5) + BCrypt                         │
│ ├─ Redis Pub/Sub para eventos assíncronos                   │
│ └─ Web3j para transações blockchain                         │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│ PERSISTÊNCIA                                                │
│ ├─ Hibernate ORM (JPA 3.x)                                  │
│ ├─ Flyway: Schema versionado (migrations)                   │
│ ├─ Envers: Auditoria de mudanças (temporal)                │
│ └─ MySQL/PostgreSQL drivers                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 🌐 Fluxo de Dados — Exemplo: Autenticação + Gamificação

```
1. CLIENTE
   └─ POST /api/usuarios/login {email, senha}

2. CONTROLLER
   └─ UsuarioController.loginPost()

3. SERVICE
   ├─ UsuarioService.buscarPorEmail()
   ├─ BCryptPasswordEncoder.matches()
   └─ AuthService.authenticate() -> JwtService.gerarToken()

4. REPOSITORY
   └─ UsuarioRepository.findByEmail() [BD: SELECT]

5. PERSISTÊNCIA
   └─ MySQL: usuario table

6. RESPONSE
   └─ Cliente recebe {token: JWT, tipo: Bearer}

7. PRÓXIMAS REQUISIÇÕES
   ├─ Header: Authorization: Bearer {token}
   ├─ JwtFiltro intercepta
   ├─ Valida signature e claims
   └─ Autoriza acesso ao endpoint

8. EVENTOS ASSÍNCRONOS (exemplo: desafio concluído)
   ├─ DesafioService.marcarConcluído()
   ├─ Redis.publish("desafio.concluido", {userId, desafioId})
   ├─ GamificacaoListener consome evento
   ├─ GamificacaoService.calcularPontos()
   └─ UserService.atualizarReputacao()
```

---

## 🧪 Estratégia de Testes

| Nível | Ferramentas | Escopo | Velocidade |
|-------|-------------|--------|-----------|
| **Unit** | JUnit 5 + Mockito | Serviços isolados | < 1s |
| **Integration** | Spring Test + H2 | Controller + Persistência | < 5s |
| **Fake** | FakeUserRepository | Sem banco real | < 100ms |
| **E2E** | MockMvc | Fluxo completo HTTP | < 10s |

**Padrão: Reflection para tipos genéricos**
```java
// Para suportar entidades que não expõem setId() público:
entity.getClass().getDeclaredMethod("setId", Long.class)
    .invoke(entity, idCounter++);
```

---

## 🚀 Deploy — Ciclo de Vida

```
┌─ LOCAL (docker-compose.yml)
│  ├─ App: Java + Spring Boot
│  ├─ DB: MySQL 8.0
│  ├─ Cache: Redis local
│  ├─ Proxy: Traefik
│  └─ Observabilidade: Prometheus + Grafana + cAdvisor

┌─ CI/CD (GitHub Actions)
│  ├─ Build: mvn clean compile
│  ├─ Test: mvn test (JUnit + Mockito)
│  ├─ SonarQube: Qualidade de código
│  └─ Push: Docker image → Registry

┌─ PRODUÇÃO (Railway + Nginx)
│  ├─ App: Container Spring Boot
│  ├─ DB: MySQL (managed)
│  ├─ Cache: Upstash Redis
│  ├─ Proxy: Nginx
│  ├─ Observabilidade: Prometheus/Grafana
│  └─ Blockchain: Acesso a rede pública
```

---

## 📈 Observabilidade

```
┌────────────────────────────────────────────────────┐
│ Spring Boot Actuator                               │
│ └─ /actuator/health, /actuator/metrics            │
└────────────┬─────────────────────────────────────┘
             │
    ┌────────▼───────────┐
    │ Prometheus         │
    │ (coleta métricas)  │
    └────────┬───────────┘
             │
    ┌────────▼──────────────────┐
    │ Grafana                    │
    │ (painéis de monitoração)   │
    │ ├─ Latência                │
    │ ├─ Taxa de erro            │
    │ ├─ Throughput              │
    │ └─ Uso de recursos         │
    └────────────────────────────┘

SonarQube (qualidade estática)
├─ Cobertura de código
├─ Bugs e vulnerabilidades
├─ Dívida técnica
└─ Padrões SOLID
```

---

## 🔐 Segurança

| Camada | Tecnologia | Função |
|--------|-----------|--------|
| **Autenticação** | JWT (jjwt 0.11.5) | Tokens stateless com expiração |
| **Criptografia** | BCryptPasswordEncoder | Hash de senhas (spring-security) |
| **Autorização** | Spring Security | Políticas por role (@Secured, @PreAuthorize) |
| **Interceptação** | JwtFiltro custom | Valida token em cada requisição protegida |
| **CORS** | SecurityConfig | Controla origens permitidas (variável de env) |
| **Auditoria** | Hibernate Envers | Rastreia quem/quando mudou cada entidade |

---

## 📚 Stack Frontend

```
React 18
├─ UI Components
├─ React Router DOM 6 (rotas + proteção)
├─ Axios (cliente HTTP com interceptadores JWT)
├─ Context API (estado global: auth, user, theme)
└─ TailwindCSS + PostCSS (estilização)

Vite 7
├─ Hot Module Replacement (HMR)
├─ Build otimizado
└─ Deploy: GitHub Pages

Flutter + Dart (Mobile)
├─ UI nativa iOS/Android
├─ Flutter Secure Storage (guarda token)
└─ HTTP client com JWT headers
```

---

## 📊 Stack Analytics (Streamlit)

```
Python + Streamlit
├─ Painéis interativos
├─ requests (consome API backend)
├─ PyJWT (valida tokens)
├─ pandas + numpy (processamento de dados)
├─ plotly (gráficos)
└─ Pillow (imagens)
```

---

## 🎯 Versões Críticas (2026)

| Tecnologia | Versão | Motivo |
|---|---|---|
| Java | 17 (LTS) | Suporte estendido até 2029 |
| Spring Boot | 3.5.6 | Versão estável com features recentes |
| Maven | 3.x (mvnw) | Wrapper garante consistência |
| MySQL | 8.0 | JSON support, window functions |
| Redis | 7.x+ | Pub/Sub robusto + Streams |
| Hibernate | 6.6.29 | JPA 3.x, performance otimizada |
| Docker | 24.x+ | Suporte a buildkit |
| Node.js | 20 LTS | Via GitHub Actions |
| Python | 3.11+ | Para Streamlit |

---

## ✅ Checklist de Conformidade

- [x] Java 17 LTS em uso (versionado em pom.xml)
- [x] Spring Boot 3.5.6 (stável e atualizado)
- [x] JPA com Hibernate + Envers (auditoria)
- [x] Flyway (migrações versionadas)
- [x] Redis Pub/Sub (eventos assíncronos)
- [x] JWT stateless (segurança)
- [x] JUnit 5 + Mockito (testes)
- [x] H2 in-memory (testes isolados)
- [x] Docker + Compose (local e prod)
- [x] Prometheus + Grafana (observabilidade)
- [x] SonarQube (qualidade)
- [x] GitHub Actions (CI/CD)
- [x] Fake Repositories (mocking robusto)
- [x] Test Data Factory (builders padrão)

---

## 🔗 Documentação Relacionada

- [TECNOLOGIAS_RESUMO.md](TECNOLOGIAS_RESUMO.md) — Listagem detalhada de dependências
- [docs/packages/backend-test-architecture.md](docs/packages/backend-test-architecture.md) — SDD de padrões de teste
- [docs/packages/backend-quick-reference.md](docs/packages/backend-quick-reference.md) — Cheat sheet para desenvolvedores
- [docs/packages/backend-workflows.md](docs/packages/backend-workflows.md) — Diagramas de fluxo

