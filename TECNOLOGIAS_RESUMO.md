# Tecnologias do Mifica — Stack Completo

## Backend (Spring Boot 3 + Java 21)

| Tecnologia | Função | Motivação |
|---|---|---|
| **Java 21** | Linguagem principal | Tipagem forte, JVM madura, ecossistema enterprise |
| **Spring Boot 3.5.6** | Framework web/MVC | Convenção > configuração, reduz boilerplate |
| **Spring Security** | Autenticação/autorização | OAuth2-ready, filtros customizáveis |
| **JWT (jjwt 0.11.5)** | Autenticação stateless | Escalável, descentralizado, HMAC-SHA256 |
| **Spring Data JPA** | Persistência ORM | Reduz SQL manual, mapping automático |
| **Hibernate ORM** | Persistência motor | Battle-tested, lazy loading, query optimization |
| **Hibernate Envers** | Auditoria de entidades | Rastreamento de mudanças, histórico completo |
| **Flyway + Flyway MySQL** | Migrations versionadas | Versionamento de schema, rollback seguro |
| **Hibernate Validator 8.0.1** | Validação de dados | Bean Validation, anotações customizáveis |
| **MySQL Connector** | Driver MySQL | ACID, índices, compatível com Render |
| **PostgreSQL Connector** | Driver PostgreSQL | Suporte a múltiplos bancos (Render Database) |
| **H2 Database** | Banco em-memória | Testes locais rápidos, sem dependências |
| **Redis Pub/Sub (Upstash)** | Mensageria assíncrona | Resiliente, managed, sem infra Kafka |
| **Spring Data Redis** | Cache e Pub/Sub | Integração nativa com Redis/Upstash |
| **Swagger/OpenAPI 2.5.0** | Documentação automática | UI interativa, client generation |
| **Spring Boot Actuator** | Monitoramento/health | Endpoints de métricas e liveness/readiness |
| **Spring Boot Mail** | Envio de e-mail | Confirmação de cadastro, notificações |
| **Web3j 4.9.8** | Integração blockchain | Conexão com smart contracts Ethereum |
| **Jakarta EL 4.0.2** | Expression Language | Suporte a EL em validações e templates |
| **Maven 3.9+** | Build tool | Dependências declarativas, profiles, plugins |

## Frontend (React 18 + Vite + TailwindCSS)

| Tecnologia | Função | Motivação |
|---|---|---|
| **React 18** | UI Framework | Component-based, virtual DOM, hooks, suspense |
| **Vite 5+** | Build tool / Dev server | ESM nativo, HMR rápido, bundles otimizados, Rollup |
| **TailwindCSS 3+** | Utility-first CSS | Composição rápida, design system, dark mode, responsividade |
| **React Router DOM** | Roteamento SPA | Lazy loading, protected routes, nested routing |
| **Axios** | Cliente HTTP | Interceptors JWT, erros centralizados, retry automático |
| **React Context API** | State management | Leve para autenticação global, sem dependências extras |
| **Responsividade Mobile-first** | Design adaptive | TailwindCSS breakpoints, viewport meta |
| **PostCSS** | Processamento CSS | Compatibilidade cross-browser, transformações |
| **npm / yarn** | Package manager | Gerenciamento de dependências frontend |

## Mobile (Flutter + Dart)

| Tecnologia | Função | Motivação |
|---|---|---|
| **Flutter 3.5+** | Cross-platform | iOS/Android, hot reload, UI material design, Performance |
| **Dart 3+** | Linguagem | Tipagem forte, async/await nativo, null-safety, compilação JIT/AOT |
| **Flutter Secure Storage** | Armazenamento seguro | Keychain (iOS) / Keystore (Android), criptografia |
| **HTTP Client** | Requisições API | Injeção automática de token JWT, timeout |
| **Material Design 3** | Design system | Componentes e padrões visuais consistentes |

## Infraestrutura & DevOps

| Tecnologia | Função | Motivação |
|---|---|---|
| **Docker** | Containerização | Isolamento, reproducibilidade, multi-stage builds |
| **Docker Compose** | Orquestração local | Replica produção em dev (MySQL, Redis, Backend, Traefik) |
| **GitHub Actions** | CI/CD | Workflow YAML, triggers automáticos, deploy contínuo |
| **Render** | PaaS backend | Zero-ops, auto-scaling, HTTPS grátis, conexão DB |
| **GitHub Pages** | SPA hosting | CDN global, deploy via git, gratuito, cache automático |
| **Traefik** | Reverse proxy | Load balancing, service discovery automático, middlewares |
| **MySQL (Produção)** | Banco relacional | ACID, índices, replicação, backups gerenciados |
| **Redis (Upstash)** | Cache + Pub/Sub | Gerenciado, resiliente, clustering, TLS |
| **Nginx** | Web server | Produção, reverse proxy, cache, compressão gzip |
| **Git + GitHub** | VCS | Versionamento, CI/CD hooks, issues, PRs |
| **Variáveis de Ambiente** | Configuração | 12-Factor App, segurança (sem secrets hardcoded) |

## Análise & Documentação

| Tecnologia | Função | Motivação |
|---|---|---|
| **Python 3.10+** | Linguagem backend alternativa | Prototipagem, scripts, data science |
| **Streamlit** | Dashboard interativo | Prototipagem rápida, plots interativos, zero boilerplate |
| **Pandas** | Análise de dados | DataFrames, agregações, limpeza de dados |
| **Matplotlib / Plotly** | Visualizações | Gráficos interativos, dashboards, analytics |
| **Spec-Kit** | Spec-driven development | Framework para PRDs, implementation plans, executável |
| **Markdown** | Documentação | Versionável, legível, Git-native, integrada no repo |
| **GitHub Wiki** | Documentação colaborativa | Hosting integrado, versioning automático |

---

## ✅ Resumo de Implementação

### Backend (Totalmente Funcional em Produção)
✅ **21 tecnologias** integradas (ver tabela acima)  
✅ **45+ classes Java** com análise CDD/ICP  
✅ **API REST completa** (CRUD + autenticação + gamificação + blockchain)  
✅ **Persistência multi-banco** (MySQL produção, PostgreSQL alternativa, H2 testes)  
✅ **Segurança hardened** (JWT, Spring Security, CORS, validação)  
✅ **Assincronia robusta** (Redis Pub/Sub com Upstash)  
✅ **Auditoria completa** (Hibernate Envers)  
✅ **Migrations automáticas** (Flyway)  
✅ **Documentação executável** (Swagger/OpenAPI)  
✅ **Monitoramento pronto** (Actuator, estruturado para Prometheus)  

### Frontend (GitHub Pages)
✅ **9 tecnologias** (React, Vite, TailwindCSS, Router, Context, Axios)  
✅ **SPA responsiva** com autenticação JWT  
✅ **Dark mode integrado**  
✅ **Lazy loading de rotas**  
✅ **Mobile-first design**  

### Mobile (Base Estruturada)
✅ **Flutter 3.5+** com Dart null-safe  
✅ **HTTP interceptor customizado** com injeção de token  
✅ **Segurança (Flutter Secure Storage)**  

### DevOps (Totalmente Automatizado)
✅ **Docker multi-stage** otimizado  
✅ **Docker Compose** (dev + produção)  
✅ **CI/CD via GitHub Actions**  
✅ **Infraestrutura 12-Factor App**  
✅ **Deploys zero-downtime**  

---

## 🧠 CDD/ICP — Cognitive-Driven Development

**Cognitive-Driven Development (CDD)** com **Índice de Complexidade Percebida (ICP)** aplicado a 45+ classes Java:

```java
// ICP-TOTAL: 7    ← Total de pontos de decisão
// ICP-01: Bifurcação/condição/delegação/loop #1
// ICP-02: Bifurcação/condição/delegação/loop #2
// ...
```

Inspiração: [Análise de complexidade cognitiva da Zup](https://zup.com.br/blog/cognitive-driven-development-cdd/)

### Maiores Complexidades

| Classe | Linhas | ICP | Razão |
|---|---|---|---|
| `UsuarioService` | 351 | 7 | Reputação, recompensas, validações |
| `UsuarioController` | 417 | 6 | Cadastro, login, gamificação, JWT, admin |
| `SecurityConfig` | 115 | 3 | CORS, autorização granular |
| `JwtFiltro` | 104 | 3 | Exclusão de rotas, bearer extraction |
| `RedisConfig` | 91 | 3 | Resilência, listener custom |

### Benefícios

✅ **Refactoring Guiado** — Identifica métodos candidatos a separação  
✅ **Onboarding** — Novos devs veem pontos críticos rapidamente  
✅ **Testing** — Alta ICP = testabilidade deve ser prioridade  
✅ **Auditoria** — Rastreabilidade de decisões técnicas  

---

## 📐 Spec-Driven Development (SDD) com SpecKit

**SpecKit** em [tools/spec-kit/](tools/spec-kit/) apoia **Specification-Driven Development**:

### Princípios SDD

1. **Especificações como Artefato Primário**  
   PRDs e design docs em [docs/packages/](docs/packages/) rastreiam decisões. Código é expressão executável.

2. **Comunicação em Mais Alto Nível**  
   Time discute features, não syntax. IA gera implementações a partir de specs.

3. **Mudança Sistemática**  
   Quando specs mudam, testes, APIs e docs regeneram automaticamente.

4. **Executabilidade**  
   Specs devem ser precisas o suficiente para gerar código correto.

### Estrutura SDD em Mifica

```
docs/packages/
├── 00-INDEX.md              # Índice principal
├── backend-app.md           # Aplicação principal
├── backend-controller.md    # Controllers REST (CDD)
├── backend-service.md       # Services & business logic (CDD)
├── backend-entity.md        # Domain model & JPA (CDD)
├── backend-config.md        # Security, CORS, Redis
├── backend-util.md          # JWT, Filters (CDD)
├── backend-repository.md    # Data access
├── backend-dto.md           # Data transfer objects
├── backend-filter.md        # Servlet filters
├── backend-redis.md         # Redis Pub/Sub
├── backend-blockchain.md    # Blockchain
├── frontend-components.md   # React components
├── frontend-pages.md        # Page structures
├── frontend-routes.md       # Routing logic
├── frontend-services.md     # HTTP client
├── frontend-context.md      # State management
├── frontend-hooks.md        # Custom hooks
├── frontend-styles.md       # Design tokens
├── frontend-utils.md        # Utilities
├── streamlit-*.md           # Modules (Python)
└── flutter-*.md             # Modules (Dart)
```

---

## Princípios Arquiteturais

### 1. **Segurança por Padrão**
✅ JWT em todas as rotas protegidas  
✅ CORS centralizado, configurável por variável de ambiente  
✅ Sem credenciais hardcoded (12-Factor App)  
✅ Spring Security com filtros customizados

### 2. **Escalabilidade Assíncrona**
✅ Redis Pub/Sub para gamificação não-bloqueante  
✅ Controllers retornam rápido; processamento delegado  
✅ Upstash (managed) reduz overhead operacional

### 3. **12-Factor App**
✅ Tudo é configuração por variáveis de ambiente  
✅ Zero secrets no repositório  
✅ Logs stdout, sem arquivos lokais  
✅ Builds reproducíveis via Docker

### 4. **Observabilidade**
✅ Logging estruturado com SLF4J  
✅ Monitoramento pronto para Prometheus  
✅ Traçabilidade de requisições

### 5. **API-First Design**
✅ Swagger/OpenAPI automático  
✅ Contratos de DTO versionados  
✅ Acoplamento mínimo frontend/backend

---

## Por que esta stack?

| Escolha | Alternativa | Rationale |
|---|---|---|
| Spring Boot | Django/FastAPI | Java/JVM = produção pronta, ecossistema maior |
| JWT | Sessions | Stateless, escalável, descentralizado |
| React | Vue/Angular | Dominância market, ecosystem consolidado |
| TailwindCSS | Bootstrap | Utilities-first, composição, dark mode |
| Redis Pub/Sub | RabbitMQ/Kafka | Simplicidade, managed em Upstash, zero infra |
| Render | AWS/Azure | Zero-ops, pricing previsível, deploys rápidos |
| Spec-Kit | Jira/Confluence | Versionável, Git-native, executável |
| CDD/ICP | Sonar/Codacy | Documentado, rastreável, humanizável |

---

## 💼 Sinal de diferencial para recrutadores e gestão técnica

Este projeto não apresenta apenas stack e código; ele evidencia **maturidade de engenharia**:

- **Especificação antes da implementação (SDD)** com documentação modular rastreável.
- **Complexidade cognitiva explícita (CDD/ICP)** em classes críticas do backend.
- **Critérios de aceitação por pacote**, reduzindo ambiguidade para manutenção e onboarding.
- **Separação de responsabilidades** alinhada com clean code (camadas e contratos claros).

Evidências rápidas:
- [docs/packages/00-INDEX.md](docs/packages/00-INDEX.md)
- [docs/packages/backend-service.md](docs/packages/backend-service.md)
- [docs/packages/backend-controller.md](docs/packages/backend-controller.md)

---

## 🗺️ Roadmap Tecnológico (2026+)

- [ ] **Prometheus + Grafana** — Observabilidade de métricas
- [ ] **Kubernetes** — Auto-scaling, rolling updates, self-healing
- [ ] **Keycloak** — Identity Provider para SSO
- [ ] **gRPC** — Comunicação inter-serviço performática
- [ ] **Event Sourcing** — Auditoria completa
- [ ] **CQRS** — Separação read/write
- [ ] **Machine Learning** — Recomendações personalizadas

---

## 📝 Referências

- **Spec-Driven Development**: [SpecKit GitHub](https://github.com/github/spec-kit)
- **Cognitive-Driven Development**: [Zup CDD Blog](https://zup.com.br/blog/cognitive-driven-development-cdd/)
- **12-Factor App**: [12factor.net](https://12factor.net/)
- **Spring Boot Docs**: [spring.io](https://spring.io/projects/spring-boot)
- **React Docs**: [react.dev](https://react.dev/)
