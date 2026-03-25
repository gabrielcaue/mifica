# Tecnologias do Mifica — Stack Completo

## Backend (Spring Boot 3 + Java 21)

| Tecnologia | Função | Motivação |
|---|---|---|
| **Java 21** | Linguagem principal | Tipagem forte, JVM madura, ecossistema enterprise |
| **Spring Boot 3** | Framework web/MVC | Convenção > configuração, reduz boilerplate |
| **Spring Security** | Autenticação/autorização | OAuth2-ready, filtros customizáveis |
| **JWT** | Autenticação stateless | Escalável, descentralizado, HMAC-SHA256 |
| **Spring Data JPA** | Persistência ORM | Reduz SQL manual, mapping automático |
| **Hibernate** | Persistência motor | Battle-tested, lazy loading, query optimization |
| **MySQL** | Banco relacional | ACID, índices, compatível com Render |
| **Redis Pub/Sub (Upstash)** | Mensageria assíncrona | Resiliente, managed, sem infra Kafka |
| **Swagger/OpenAPI** | Documentação automática | UI interativa, client generation |
| **Maven** | Build tool | Dependências declarativas, profiles |

## Frontend (React 18 + Vite + TailwindCSS)

| Tecnologia | Função | Motivação |
|---|---|---|
| **React 18** | UI Framework | Component-based, virtual DOM, hooks |
| **Vite** | Build tool | ESM nativo, HMR rápido, bundles otimizados |
| **TailwindCSS** | Utility-first CSS | Composição rápida, design system, dark mode |
| **React Router DOM** | Roteamento SPA | Lazy loading, protected routes |
| **Axios** | Cliente HTTP | Interceptors JWT, erros centralizados |
| **React Context** | State management | Leve para autenticação global |
| **Responsividade** | Design adaptive | Mobile-first com TailwindCSS |

## Mobile (Flutter + Dart)

| Tecnologia | Função | Motivação |
|---|---|---|
| **Flutter** | Cross-platform | iOS/Android, hot reload, UI material design |
| **Dart** | Linguagem | Tipagem forte, async/await nativo, null-safe |
| **Flutter Secure Storage** | Armazenamento seguro | Keychain/keystore do SO |

## Infraestrutura & DevOps

| Tecnologia | Função | Motivação |
|---|---|---|
| **Docker** | Containerização | Isolamento, reproducibilidade |
| **Docker Compose** | Orquestração local | Replica produção em dev |
| **GitHub Actions** | CI/CD | Workflow YAML, triggers automáticos |
| **Render** | PaaS backend | Zero-ops, auto-scaling, HTTPS grátis |
| **GitHub Pages** | SPA hosting | CDN, deploy via git, gratuito |
| **Traefik** | Reverse proxy | Load balancing, discovery automático |

## Análise & Documentação

| Tecnologia | Função | Motivação |
|---|---|---|
| **Python + Streamlit** | Dashboard analítico | Prototipagem rápida, plots interativos |
| **Spec-Kit** | Spec-driven development | Framework para PRDs e implementation plans |
| **Markdown** | Documentação | Versionável, legível, integrada no repo |

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
