# 🧠 Mifica — Plataforma Modular de Reputação, Gamificação e Blockchain

> ⚠️ **Status: Em Manutenção** — O domínio está em manutenção. Infraestrutura resiliente com Redis Pub/Sub (Upstash), CORS centralizado, segurança por JWT, e ICPs/CDD aplicados ao backend Java.

---

## 🔗 Acesse o Projeto

| Serviço | URL |
|---|---|
| 🌐 **Frontend (GitHub Pages)** | [gabrielcaue.github.io/mifica](https://gabrielcaue.github.io/mifica/) |
| ⚙️ **Backend API** | Configure em `VITE_API_URL` |
| 📦 **Repositório** | [github.com/gabrielcaue/mifica](https://github.com/gabrielcaue/mifica) |
| 📖 **Documentação Técnica** | [docs/packages/00-INDEX.md](docs/packages/00-INDEX.md) |

---

## 📌 Resumo rápido para recrutadores

- Tecnologias e função de cada uma: [TECNOLOGIAS_RESUMO.md](TECNOLOGIAS_RESUMO.md)
- Documentação de arquitetura e módulos: [docs/packages/](docs/packages/)

### 🎯 Diferenciais (evidência rápida em seleção)

- **SDD aplicado em documentação de pacotes**: 24 documentos padronizados por escopo, contratos, critérios de aceitação e riscos.
- **CDD/ICP aplicado no backend**: 45+ classes Java com pontos de complexidade cognitiva explicitados.
- **Arquitetura orientada a manutenção**: separação clara entre `controller`, `service`, `repository`, `config`, `util` e `dto`.

**Exemplos para avaliação técnica (tech manager / coordenação):**
- [docs/packages/backend-service.md](docs/packages/backend-service.md)
- [docs/packages/backend-controller.md](docs/packages/backend-controller.md)
- [docs/packages/backend-config.md](docs/packages/backend-config.md)
- [docs/packages/00-INDEX.md](docs/packages/00-INDEX.md)

---

## 🧠 Engenharia Guiada por Compreensão Cognitiva (CDD/ICP)

**Cognitive-Driven Development (CDD)** com **Índice de Complexidade Percebida (ICP)** aplicado ao backend:

Cada classe Java com mais de 20 linhas recebeu análise de complexidade cognitiva, com comentários `// ICP-TOTAL: N` e `// ICP-XX: descrição` documentando pontos de decisão e fluxos não-lineares. Essa abordagem, inspirada em trabalhos da [Zup](https://zup.com.br/blog/cognitive-driven-development-cdd/), melhora legibilidade e facilita futuras manutenções.

- **Total de classes backend com CDD**: 45+
- **Máximo ICP por classe**: 7 (UsuarioService)
- **Cobertura**: Controllers, Services, Configs, Entities, DTOs, Filters, Utils

---

## 📐 Engenharia Guiada por Especificação (SpecKit)

Este repositório utiliza **Spec-Driven Development (SDD)** com o toolkit **SpecKit** em [tools/spec-kit](tools/spec-kit):

### Filosofia de Especificação
- Especificações são o artefato primário; código é a expressão executável.
- Manutenção do software significa evoluir especificações, não apenas código.
- Documentação técnica versionada em [docs/packages/](docs/packages/) rastreia decisões arquiteturais.

### Prática no Projeto
- Requisitos modelados como **PRDs estruturados** (Product Requirements Documents)
- Mudanças em especificações propagam automaticamente para implementação
- Testes nascidos da especificação, não como artefato posterior
- Pesquisa de contexto técnico integrada ao processo (constrains organizacionais, padrões, etc.)

---

## Sobre o Projeto

Desenvolvi o Mifica como uma plataforma modular que integra **reputação**, **gamificação** e **transações via blockchain**, com foco em escalabilidade, segurança e extensibilidade. O projeto é dividido em módulos independentes que se comunicam via API REST e eventos assíncronos com Redis Pub/Sub.

Meu objetivo foi construir uma aplicação que demonstrasse domínio real sobre o ciclo completo de um produto de software: desde a modelagem de domínio e arquitetura de microsserviços até o deploy em produção com CI/CD automatizado.

---

## 🚀 Funcionalidades Implementadas

### Backend (Spring Boot 3 + Java 21)
✅ API REST completa com CRUD de usuários, transações e contratos  
✅ Autenticação JWT com controle de acesso por roles (USER / ADMIN)  
✅ Sistema de reputação com cálculo dinâmico e conquistas desbloqueáveis  
✅ Gamificação com missões diárias, recompensas e pontuação  
✅ Integração com Redis Pub/Sub para eventos assíncronos (Upstash em produção)  
✅ Persistência com MySQL + Hibernate/JPA  
✅ Documentação automática com Swagger/OpenAPI  
✅ Spring Security com filtros JWT customizados e CORS centralizado  
✅ **ICPs/CDD aplicados** — Complexidade cognitiva documentada em 45+ classes  
✅ Configuração segura via variáveis de ambiente (12-Factor App)  

### Frontend (React 18 + Vite + TailwindCSS)
✅ SPA responsiva com login, cadastro, dashboard e perfil de usuário  
✅ Painel administrativo com controle de usuários (ADMIN only)  
✅ Rotas protegidas por autenticação JWT  
✅ Deploy automatizado no GitHub Pages  
✅ Design mobile-first com suporte a responsividade  

### Flutter (Base inicial — 84 linhas com CDD)
✅ Cliente HTTP com injeção automática de token JWT  
✅ Tratamento centralizado de erros e fallback de autenticação  
✅ Segurança com Flutter Secure Storage  

### Infraestrutura & DevOps
✅ **Docker** — Multi-stage builds otimizados para backend e frontend  
✅ **Docker Compose** — Orquestração local de MySQL, Redis, backend e frontend  
✅ **Redis Pub/Sub (Upstash)** — Pub/Sub assíncrono resiliente para gamificação  
✅ **CI/CD com GitHub Actions** — Pipeline de build, testes e deploy automatizado  
✅ **Infraestrutura em nuvem** — Backend Java 21 em produção com HTTPS  
✅ **GitHub Pages** — Frontend estático com deploy contínuo  
✅ **Variáveis de ambiente** — Zero credenciais no código (princípio 12-Factor)  

### Painel Analítico (Streamlit + Python)
✅ Dashboard interativo com visualizações de dados  
✅ Integração com API backend  
✅ Análise de reputação, gamificação e transações  

---

## 🔄 Atualizações Recentes

### Março/2026 — Ciclo CDD & Especificação

- **Implementei CDD (Cognitive-Driven Development) com ICPs** — Todas as 45+ classes Java >20 linhas receberam análise de complexidade cognitiva. Cada ponto de decisão, bifurcação condicional e delegação assíncrona foi documentado com comentários `// ICP-XX`. Máximo ICP encontrado: 7 em `UsuarioService` (gerenciamento de reputação, recompensas, alteração de senha).

- **Refatorei documentação técnica para SDD** — Estruturei especificações em [docs/packages/](docs/packages/) seguindo abordagem spec-driven, com rastreabilidade de decisões arquiteturais em cada módulo (backend-service, backend-controller, backend-config, etc.).

- **Solidifiquei segurança por autenticação centralizada** — JWT em todas as rotas protegidas, CORS configurável por variável de ambiente, e eliminação de dados sensíveis de repositório.

### Período Anterior (Fevereiro/2026)

- **Migrei de Apache Kafka para Redis Pub/Sub** — Substituí toda a infraestrutura Kafka por Redis Pub/Sub com Upstash como provedor gerenciado, simplificando a stack sem perder comunicação assíncrona.

- **Refatorei configuração de segurança HTTP** — Unifiquei CORS no SecurityConfig com `allowedOriginPatterns`, liberação explícita de preflight OPTIONS, e endpoints públicos para cadastro/login.

- **Eliminei credenciais hardcoded** — 100% de secrets agora via variáveis de ambiente (JWT_SECRET, DATABASE_PASSWORD, REDIS_PASSWORD, etc.), seguindo 12-Factor App.

- **Corrigi perfil de produção** — Removido dialeto PostgreSQL incorreto, ajustado ddl-auto para update, eliminados context-path duplicados.

---

## 🏗️ Arquitetura

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   Frontend       │     │   Backend        │     │   Streamlit      │
│   React + Vite   │────▶│   Spring Boot 3  │◀────│   Python         │
│   GitHub Pages   │     │   Backend API    │     │   Dashboard      │
└──────────────────┘     └────────┬─────────┘     └──────────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼             ▼             ▼
             ┌───────────┐ ┌───────────┐ ┌───────────┐
             │  Database │ │  Redis    │ │  Traefik  │
             │  MySQL    │ │  Upstash  │ │  Proxy    │
             └───────────┘ └───────────┘ └───────────┘
```

- **Backend (Spring Boot 3 / Java 21)** → Lógica de negócio, reputação, gamificação e blockchain
- **Redis Pub/Sub (Upstash)** → Eventos assíncronos para gamificação em tempo real
- **Frontend (React + Vite)** → Interface do usuário com SPA
- **Streamlit (Python)** → Painel administrativo e visualizações de dados
- **Database (MySQL)** → Persistência de dados em produção
- **Docker Compose + Traefik** → Orquestração e roteamento local
- **GitHub Actions** → CI de backend e deploy automático em produção

---

## 📁 Estrutura do Projeto

```
mifica/
├── mifica-backend/        # API REST — Spring Boot 3, Java 21
│   ├── src/main/java/     # Controllers, Services, Entities, Redis Pub/Sub
│   ├── src/main/resources/ # application.properties, application-prod.properties
│   ├── Dockerfile          # Build local
│   └── Dockerfile.prod     # Build produção (multi-stage)
├── mifica-frontend/       # SPA — React, Vite, TailwindCSS
│   ├── src/               # Components, Pages, Services, Context
│   └── .env.production    # URL da API em produção
├── mifica-streamlit/      # Dashboard — Python, Streamlit
├── tools/spec-kit/        # Submódulo de apoio a spec-driven development
├── .github/workflows/     # CI/CD — GitHub Actions
├── docker-compose.yml     # Orquestração local (MySQL, Redis, Traefik)
└── start-dev.sh           # Script para subir ambiente de desenvolvimento
```

---

## 🧪 Como Rodar Localmente

```bash
# Clone o repositório
git clone https://github.com/gabrielcaue/mifica.git
cd mifica

# Suba os containers (Redis + MySQL + Backend + Streamlit + Traefik)
./start-dev.sh
```

| Serviço | URL Local |
|---|---|
| Backend | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| Streamlit | http://localhost:8501 |
| Swagger | http://localhost:8080/swagger-ui.html |

---

## ✅ O que Já Está Implementado (e impressiona)

| Tecnologia | Uso no Projeto | Status |
|---|---|---|
| **Java 21 + Spring Boot 3** | API REST, Security, JPA | ✅ Produção |
| **Redis Pub/Sub (Upstash)** | Eventos assíncronos de gamificação | ✅ Produção |
| **Docker + Multi-stage Build** | Containers otimizados | ✅ Produção |
| **CI/CD (GitHub Actions)** | Deploy automático backend + frontend | ✅ Produção |
| **Database (MySQL)** | Persistência relacional | ✅ Produção |
| **JWT + Spring Security** | Autenticação e autorização por roles | ✅ Produção |
| **React + Vite + TailwindCSS** | Frontend SPA responsivo | ✅ Produção (GitHub Pages) |
| **Swagger/OpenAPI** | Documentação automática da API | ✅ Implementado |
| **Cloud Provider** | Hospedagem do backend em nuvem | ✅ Produção |
| **Traefik** | Reverse proxy / load balancer | ✅ Dev local |
| **Streamlit** | Dashboard administrativo Python | ✅ Implementado |
| **12-Factor App** | Configuração por variáveis de ambiente | ✅ Produção |

---

## 🗺️ Próximos Passos (Roadmap)

- **📊 Prometheus + Grafana (Observabilidade)**  
  Monitoramento de métricas do backend (latência, throughput, erros, uso de recursos). Qualquer sistema em produção precisa de observabilidade — é o primeiro sinal de maturidade operacional.

- **🧵 Kubernetes (GKE / EKS / AKS)**  
  Orquestração de containers com auto-scaling, rolling updates e self-healing. Migrar de Docker Compose para K8s demonstra capacidade de operar em escala real.

- **🔐 Keycloak (Identity Provider)**  
  Substituir JWT manual por um identity provider centralizado com OAuth2/OIDC, SSO e gestão de usuários. Mostra entendimento de segurança enterprise.

- **🔍 ElasticSearch (Busca e Analytics)**  
  Busca full-text em transações e eventos, dashboards de analytics em tempo real. Complementa o Kafka como consumer de eventos.

- **🔄 Istio / Service Mesh**  
  Controle de tráfego, circuit breaker, mTLS e observabilidade entre microsserviços. Faz sentido quando houver múltiplos serviços independentes.

---

## 💡 Por que Este Projeto se Destaca

1. **Não é um CRUD genérico** — Tem Redis Pub/Sub, gamificação, blockchain e eventos assíncronos
2. **Está em produção** — Backend em nuvem, frontend no GitHub Pages, CI/CD rodando
3. **Segue boas práticas** — 12-Factor, variáveis de ambiente, multi-stage Docker
4. **Arquitetura real** — Serviços com mensageria assíncrona, não monolito simples
5. **Completo** — Java + React + Python + DevOps + Cloud

---

## 📫 Contato

Desenvolvido por **Gabriel Cauê**  
📧 Entre em contato pelo [GitHub](https://github.com/gabrielcaue)
[Linkedin] (https://www.linkedin.com/in/gabrielcaues)

---

## 🛠️ Status

**Em manutenção.**

