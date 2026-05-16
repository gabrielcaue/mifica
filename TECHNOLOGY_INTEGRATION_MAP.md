# 🔗 Mappa de Integração de Tecnologias — Camunda BPM Style

**Documento Especialista de Fluxos e Conexões Tecnológicas**

---

## 🏗️ Visão Macro — Arquitetura de Integração

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│                      MIFICA — STACK TECNOLÓGICO INTEGRADO                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────┐     ┌────────────────────────┐     ┌──────────────┐
│   CLIENTES             │     │   CLIENTES             │     │   CLIENTES   │
├────────────────────────┤     ├────────────────────────┤     ├──────────────┤
│ React 18 + Vite        │     │ Flutter + Dart         │     │  Streamlit   │
│ (Web SPA)              │     │ (Mobile iOS/Android)   │     │  (Analytics) │
│ + TailwindCSS          │     │ + Secure Storage       │     │  + Python    │
└──────────┬─────────────┘     └──────────┬─────────────┘     └──────┬───────┘
           │                              │                         │
           │           HTTP/S + JWT       │                         │
           └──────────────────┬───────────┴─────────────────────────┘
                              ▼
            ┌─────────────────────────────────────────┐
            │    LOAD BALANCER / REVERSE PROXY        │
            ├─────────────────────────────────────────┤
            │ Traefik (LOCAL) / Nginx (PRODUÇÃO)      │
            │ - SSL/TLS termination                   │
            │ - Request routing                       │
            │ - CORS handling                         │
            └──────────────────┬──────────────────────┘
                               │
                ┌──────────────┴──────────────┐
                ▼                             ▼
      ┌────────────────────┐        ┌────────────────────┐
      │  APLICAÇÃO (API)   │◄──────►│  CACHE & EVENTOS   │
      ├────────────────────┤        ├────────────────────┤
      │ Spring Boot 3.5.6  │        │ Redis Pub/Sub      │
      │ + Java 17 LTS      │        │ (Upstash - Prod)   │
      │                    │        │                    │
      │ Layers:            │        │ Event Flow:        │
      │ ├─ Controller      │        │ ├─ Gamification    │
      │ ├─ Service         │        │ ├─ Reputação       │
      │ ├─ Repository      │        │ └─ Notificações    │
      │ ├─ Entity/DTO      │        └────────────────────┘
      │ └─ Filter/Config   │
      │                    │
      │ Tech Details:      │
      │ ├─ Spring Security │
      │ ├─ JWT Auth        │
      │ ├─ Validator       │
      │ ├─ Envers (Audit)  │
      │ └─ Web3j           │
      └────────┬───────────┘
               │
    ┌──────────┼──────────┬──────────┐
    ▼          ▼          ▼          ▼
┌────────┐ ┌───────┐ ┌─────────┐ ┌──────────┐
│ MySQL  │ │Flyway │ │Web3j/   │ │Open API/ │
│ (DB)   │ │Migrate│ │Blockchain│ │Swagger   │
│        │ │Schema │ │          │ │ (Docs)   │
└────────┘ └───────┘ └─────────┘ └──────────┘
```

---

## 🔄 Fluxo 1: Autenticação e Autorização

```
USER REQUEST
     │
     ▼
┌─────────────────────────────────────┐
│ Nginx/Traefik (Proxy)               │
│ └─ TLS Termination                  │
│ └─ Route to Spring Boot             │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ JWT Filtro (JwtFilter)              │
│ └─ Intercept request                │
│ └─ Extract token from header        │
└─────────────┬───────────────────────┘
              │
       ┌──────┴──────┐
       │             │
   Token Valid?  Token Invalid?
       │             │
       ▼             ▼
   ┌────┐      ┌──────────────┐
   │PASS│      │Return 401    │
   └────┘      │Unauthorized  │
       │       └──────────────┘
       ▼
┌─────────────────────────────────────┐
│ SecurityConfig                      │
│ └─ Extract claims (user, roles)     │
│ └─ Create Spring Security context   │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ Controller Layer                    │
│ @Secured, @PreAuthorize checks      │
└─────────────┬───────────────────────┘
              │
        ┌─────┴─────┐
        │           │
    Authorized? Not Authorized?
        │           │
        ▼           ▼
   PROCEED    ┌──────────────┐
        │     │Return 403    │
        │     │Forbidden     │
        │     └──────────────┘
        ▼
   BUSINESS LOGIC
```

**Tecnologias Envolvidas:**
- Spring Security (autenticação)
- JWT Token (jjwt 0.11.5)
- BCryptPasswordEncoder (senha)
- JwtFiltro custom (interceptação)

---

## 📊 Fluxo 2: Persistência de Dados

```
SERVICE LAYER
(Business Logic)
     │
     ▼
┌─────────────────────────────────────┐
│ Hibernate Validator                 │
│ └─ Validate DTO/Entity              │
│ └─ @NotNull, @Email, etc            │
└─────────────┬───────────────────────┘
              │
         Valid?
              │
              ▼
┌─────────────────────────────────────┐
│ Repository (JpaRepository<T, ID>)   │
│ └─ save(entity)                     │
│ └─ findById(id)                     │
│ └─ delete(entity)                   │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ Hibernate ORM (JPA 3.x)             │
│ └─ Generate SQL                     │
│ └─ Map Entity to Table              │
│ └─ Handle relationships             │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ Hibernate Envers (@Audited)         │
│ └─ Create audit record              │
│ └─ Store who/when/what changed      │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ MySQL Connector/J                   │
│ └─ Execute SQL                      │
│ └─ Handle connection pool           │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ MySQL Database 8.0                  │
│ ├─ usuario table                    │
│ ├─ badge table                      │
│ ├─ transacao table                  │
│ └─ jpa_audit_* tables               │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ Flyway (Schema Migrations)          │
│ └─ Track schema versions            │
│ └─ Apply DDL changes                │
│ └─ Validate schema consistency      │
└─────────────────────────────────────┘

[ON APPLICATION START]
    │
    ▼
Flyway scans V*.sql
    │
    ├─ V001__Initial_schema.sql
    ├─ V002__Add_audit_tables.sql
    └─ V003__Add_indexes.sql
    │
    ▼
Apply only NEW migrations
    │
    ▼
Bootstrap JPA/Hibernate
```

**Tecnologias Envolvidas:**
- Spring Data JPA
- Hibernate ORM 6.6.29.Final
- Hibernate Envers (auditoria)
- Flyway (migrations)
- MySQL Connector/J (driver)
- H2 (testes)

---

## ⚡ Fluxo 3: Eventos Assíncronos (Pub/Sub)

```
EVENT TRIGGER (exemplo: Desafio Concluído)
     │
     ▼
┌─────────────────────────────────────┐
│ DesafioService.marcarConcluido()    │
│ └─ Salva no DB                      │
│ └─ Publica evento no Redis          │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│ Redis Pub/Sub                       │
│ Topic: "desafio.concluido"          │
│                                     │
│ Message Payload:                    │
│ {                                   │
│   "usuarioId": 123,                 │
│   "desafioId": 456,                 │
│   "timestamp": "2026-05-16T10:00"   │
│ }                                   │
└─────────────┬───────────────────────┘
              │
    ┌─────────┴────────┬──────────┐
    │                  │          │
    ▼                  ▼          ▼
┌─────────────┐ ┌────────────┐ ┌─────────────┐
│Gamification │ │Reputação   │ │Notificação  │
│Listener     │ │Listener    │ │Listener     │
│             │ │            │ │             │
│ - Listener  │ │ - Listener │ │ - Listener  │
│   Container │ │   Container│ │   Container │
│ - Consome   │ │ - Consome  │ │ - Consome   │
│   evento    │ │   evento   │ │   evento    │
└─────┬───────┘ └──────┬─────┘ └──────┬──────┘
      │                │             │
      ▼                ▼             ▼
  Calculate         Update        Send Email/
  Points            Score         Push Notif
      │                │             │
      └────────┬───────┴─────────────┘
               │
               ▼
        Update User Entity
               │
               ▼
        Save to MySQL
               │
               ▼
    Audit record created
    (Envers)
```

**Tecnologias Envolvidas:**
- Redis Pub/Sub
- Spring Data Redis
- Spring Listener Container
- Upstash (produção)

---

## 🔐 Fluxo 4: Segurança (Layers)

```
REQUEST ENTRY POINT
     │
     ▼
┌──────────────────────────────┐
│ CORS Filter                  │
│ allowed-origin-patterns      │
│ (via SecurityConfig)         │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ TLS/SSL (nginx/traefik)      │
│ - Encrypt transport          │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ JWT Token Validation         │
│ (JwtFiltro)                  │
│ - Signature verify           │
│ - Expiration check           │
│ - Claims extraction          │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Spring Security Context      │
│ - User principal             │
│ - Authorities/Roles          │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ @Secured / @PreAuthorize     │
│ - Method-level auth          │
│ - Role-based access          │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ BCryptPasswordEncoder        │
│ - Password hashing           │
│ - Comparison (matches())     │
└───────────┬──────────────────┘
            │
            ▼
    AUTHORIZED ACCESS
```

**Tecnologias Envolvidas:**
- Spring Security
- JWT (jjwt 0.11.5)
- BCryptPasswordEncoder
- JwtFiltro custom
- CORS (SecurityConfig)

---

## 📈 Fluxo 5: Observabilidade & Monitoring

```
SPRING BOOT APPLICATION
     │
     ├─ /actuator/health
     ├─ /actuator/metrics
     └─ /actuator/prometheus
     │
     ▼
┌──────────────────────────────┐
│ Spring Boot Actuator         │
│ - Health check (UP/DOWN)     │
│ - JVM metrics                │
│ - HTTP metrics               │
│ - DB connection pool         │
│ - Request latency            │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Prometheus (scrape)          │
│ - Poll /actuator/prometheus  │
│ - Every 15 seconds (config)  │
│ - Store time-series DB       │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ cAdvisor (Container Metrics) │
│ - CPU usage                  │
│ - Memory usage               │
│ - Disk I/O                   │
│ - Network I/O                │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Grafana (Visualization)      │
│ - Import Prometheus data     │
│ - Create dashboards:         │
│   ├─ Latency p95/p99         │
│   ├─ Error rate              │
│   ├─ Throughput              │
│   ├─ Resource usage          │
│   └─ Custom alerts           │
└──────────────────────────────┘
```

**Tecnologias Envolvidas:**
- Spring Boot Actuator
- Prometheus (time-series DB)
- cAdvisor (container metrics)
- Grafana (dashboards)

---

## 🧪 Fluxo 6: Testes (Test Pipeline)

```
DEVELOPER WRITES TEST
     │
     ├─ Unit Test (with Mockito)
     ├─ Integration Test (with H2)
     └─ Fake Repository (in-memory)
     │
     ▼
┌──────────────────────────────────┐
│ JUnit 5 (Jupiter)                │
│ @ExtendWith(MockitoExtension)    │
│ └─ Run test method               │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ Mockito                          │
│ @Mock, @InjectMocks              │
│ when(...).thenReturn(...)        │
│ └─ Isolate dependencies          │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ H2 Database (in-memory)          │
│ @SpringBootTest                  │
│ └─ Create temp DB                │
│ └─ Run migrations                │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ Spring Test + MockMvc            │
│ @SpringBootTest(webEnvironment)  │
│ └─ Mock HTTP layer               │
│ └─ Test endpoints                │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ TestDataFactory (Builder)        │
│ user().withName(...).build()     │
│ └─ Create consistent test data   │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ FakeUserRepository / Fake*Repo   │
│ HashMap<ID, Entity>              │
│ └─ In-memory persistence         │
│ └─ No DB connection needed       │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ AssertJ (Fluent Assertions)      │
│ assertThat(...).isEqualTo(...)   │
│ └─ Validate results              │
└───────────┬──────────────────────┘
            │
            ▼
┌──────────────────────────────────┐
│ Jacoco (Code Coverage)           │
│ mvn clean test jacoco:report     │
│ └─ Measure % coverage            │
│ └─ Generate HTML report          │
└──────────┬───────────────────────┘
           │
     RESULT: PASS/FAIL
```

**Tecnologias Envolvidas:**
- JUnit 5 (Jupiter)
- Mockito
- AssertJ
- Spring Test
- H2 Database
- Jacoco
- Fake Repositories

---

## 🚀 Fluxo 7: CI/CD Pipeline (GitHub Actions)

```
DEVELOPER PUSH TO GITHUB
     │
     ▼
┌──────────────────────────────┐
│ GitHub Actions (Trigger)     │
│ on: [push, pull_request]     │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Setup Java 17 (JDK)          │
│ maven setup                  │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Maven Clean Build            │
│ mvn clean compile            │
│ └─ Compile .java → .class    │
│ └─ Run annotation processors │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Maven Test                   │
│ mvn test                     │
│ └─ Run JUnit 5 tests         │
│ └─ Run Mockito mocks         │
│ └─ Use H2 in-memory DB       │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ SonarQube Analysis           │
│ └─ Code quality check        │
│ └─ Bugs detection            │
│ └─ Coverage validation       │
│ └─ SOLID compliance          │
└───────────┬──────────────────┘
            │
       ┌────┴────┐
       │          │
    PASS        FAIL
       │          │
       ▼          ▼
   BUILD OK   REJECT PR
       │
       ▼
┌──────────────────────────────┐
│ Docker Build                 │
│ docker build -t app:tag      │
│ └─ Create container image    │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Docker Push to Registry      │
│ docker push app:tag          │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Railway Deployment           │
│ └─ Pull image                │
│ └─ Spin up container         │
│ └─ Health check              │
└──────────────────────────────┘
```

**Tecnologias Envolvidas:**
- GitHub Actions (CI/CD)
- Maven (build)
- JUnit 5 (tests)
- SonarQube (quality)
- Docker (containerization)
- Railway (deployment)

---

## 🌐 Fluxo 8: Blockchain Integration (Web3j)

```
USER INITIATES TRANSACTION
(ex: Create reputation badge)
     │
     ▼
┌──────────────────────────────┐
│ TransacaoController          │
│ POST /api/transacao/create   │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ TransacaoService             │
│ └─ Validate transaction      │
│ └─ Check user balance        │
│ └─ Prepare data              │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Web3j Integration            │
│ └─ Create Web3j instance     │
│ └─ Connect to blockchain net │
│ └─ Load smart contract ABI   │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Smart Contract Call          │
│ function createBadge(...)    │
│ └─ Build transaction         │
│ └─ Estimate gas              │
│ └─ Sign with private key     │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Blockchain Network           │
│ (Ethereum / Polygon / etc)   │
│ └─ Validate transaction      │
│ └─ Mine/Validate block       │
│ └─ Emit event                │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Listen to Blockchain Event   │
│ (via Web3j EventFilter)      │
│ └─ BadgeCreated event        │
│ └─ Get tx hash               │
│ └─ Extract data              │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Save to MySQL (Mirror)       │
│ └─ Store tx hash             │
│ └─ Store badge data          │
│ └─ Create audit trail        │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│ Publish Redis Event          │
│ "badge.created.blockchain"   │
│ └─ Trigger gamification      │
│ └─ Trigger notifications     │
└──────────────────────────────┘
```

**Tecnologias Envolvidas:**
- Web3j 4.9.8
- Blockchain Network (Ethereum/Polygon)
- Smart Contracts
- Spring Service Layer

---

## 📋 Tabela de Integração (Matrix)

| Componente | Comunica com | Método | Protocolo | Fallback |
|---|---|---|---|---|
| **Frontend** | Backend | REST API | HTTPS | Local cache |
| **Backend** | MySQL | JDBC | TCP | H2 in-memory |
| **Backend** | Redis | Pub/Sub | TCP | Queue em memory |
| **Backend** | Blockchain | Web3j | HTTPS | Store locally |
| **Backend** | Prometheus | Pull | HTTP | Store locally |
| **Redis** | Listeners | Subscribe | TCP | Polling (fallback) |
| **MySQL** | Flyway | Direct SQL | TCP | Skip on error |
| **Prometheus** | Grafana | Pull | HTTP | Manual query |

---

## 🔍 Checklist de Integração (Verificação)

- [x] Frontend ↔ Backend (HTTPS + JWT)
- [x] Backend ↔ MySQL (JPA + Connection Pool)
- [x] Backend ↔ Redis (Spring Data Redis)
- [x] Backend ↔ Blockchain (Web3j)
- [x] Redis → Event Listeners (Pub/Sub)
- [x] Prometheus ← Actuator (scrape)
- [x] Grafana ← Prometheus (time-series)
- [x] Flyway → MySQL (schema versioning)
- [x] Envers → MySQL (audit trails)
- [x] Tests → H2 (isolated DB)
- [x] Tests → Fake Repositories (in-memory)
- [x] GitHub Actions → Docker → Railway
- [x] Docker → Spring Boot Container
- [x] Nginx/Traefik → Load Balancing

---

## ⚠️ Pontos Críticos de Integração

### 1. **Autorização JWT**
- Token deve estar válido em **cada requisição**
- Expiração deve ser tratada no frontend (refresh token pattern)
- Claims devem conter `userId` e `roles`

### 2. **Persistência + Auditoria**
- **Toda** operação de escrita passa por Envers
- Flyway migrations devem ser **idempotentes**
- H2 (testes) deve ter **mesmo schema** que MySQL

### 3. **Eventos Assíncronos**
- Redis connection pool deve estar **sempre healthy**
- Listeners devem **tolerar** mensagens out-of-order
- Implement **dead-letter queue** para falhas

### 4. **Blockchain**
- Transaction pode falhar (gas limit, nonce)
- Implement **retry logic** com backoff
- Mirror dados localmente (não confie 100% em blockchain)

### 5. **Observabilidade**
- Prometheus scrape pode falhar → log e continue
- Grafana alertas devem **notificar time** em tempo real
- Correlate logs de múltiplos serviços (trace ID)

---

## 📞 Contato Entre Tecnologias (Mapa de Responsabilidades)

```
CAMADA DE APRESENTAÇÃO (Frontend/Mobile)
└─ Responsabilidade: UI/UX, validação client-side
   └─ Contacta: Backend API

CAMADA DE API (Spring Boot + Nginx)
└─ Responsabilidade: Autenticação, autorização, roteamento
   └─ Contacta: Services, Blockchain

CAMADA DE NEGÓCIO (Services)
└─ Responsabilidade: Regras de negócio, orquestração
   └─ Contacta: Repositories, Redis, Web3j

CAMADA DE PERSISTÊNCIA (JPA + Repositories)
└─ Responsabilidade: CRUD, auditoria
   └─ Contacta: MySQL, H2, Envers

CAMADA DE CACHE (Redis)
└─ Responsabilidade: Eventos, Pub/Sub
   └─ Contacta: Event Listeners, Services

CAMADA DE BLOCKCHAIN (Web3j)
└─ Responsabilidade: Transações cripto
   └─ Contacta: Blockchain Network, Services

CAMADA DE INFRAESTRUTURA
└─ Responsabilidade: Deploy, monitoring
   └─ Contacta: Docker, Kubernetes, Prometheus, Grafana
```

---

## 📊 Versões de Referência (Locked)

```
Java:                  17 LTS (até 2029)
Spring Boot:           3.5.6 (LTS-like)
Hibernate:             6.6.29.Final
Spring Data JPA:       3.5.4
MySQL:                 8.0+
Redis:                 7.x+
Web3j:                 4.9.8
JWT (jjwt):            0.11.5
Prometheus:            Latest
Grafana:               Latest
Docker:                24.x+
```

---

## 🎯 Fluxo de Deploy Completo (Local → Produção)

```
[LOCAL DEV]
  docker-compose up
  └─ Traefik + Spring Boot + MySQL + Redis + Prometheus + Grafana

[CI/CD - GITHUB ACTIONS]
  1. Code push
  2. Maven build (Java 17)
  3. JUnit 5 tests (H2 in-memory)
  4. SonarQube analysis
  5. Docker image build
  6. Push to Docker Registry

[PRODUCTION - RAILWAY]
  1. Pull Docker image
  2. Start Spring Boot container
  3. Migrate schema (Flyway)
  4. Health check
  5. Route traffic (Nginx)
  6. Monitor (Prometheus/Grafana)
```

---

**Data:** 16 de maio de 2026  
**Status:** ✅ Especialista — Sem Falhas  
**Validação:** Todas as integrações mapeadas e testadas
