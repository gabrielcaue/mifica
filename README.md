# 🧠 Mifica — Plataforma Modular de Reputação, Gamificação e Blockchain

> ✅ **Status: Online** — Infraestrutura migrada de Kafka para Redis Pub/Sub (Upstash), segurança CORS configurada e variáveis de ambiente seguras no Render.

---

## 🔗 Acesse o Projeto

| Serviço | URL |
|---|---|
| 🌐 **Frontend (GitHub Pages)** | [gabrielcaue.github.io/mifica](https://gabrielcaue.github.io/mifica/) |
| ⚙️ **Backend API (Render)** | [mifica-backend.onrender.com](https://mifica-backend.onrender.com) |
| 📦 **Repositório** | [github.com/gabrielcaue/mifica](https://github.com/gabrielcaue/mifica) |

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
✅ Integração com Redis Pub/Sub para eventos assíncronos (gamificação em tempo real)  
✅ Persistência com MySQL + Hibernate/JPA  
✅ Documentação automática da API com Swagger/OpenAPI  
✅ Spring Security com filtros customizados e CORS configurável  

### Frontend (React + Vite + TailwindCSS)
✅ SPA com login, cadastro, dashboard e perfil  
✅ Painel administrativo com controle de usuários  
✅ Rotas protegidas por autenticação  
✅ Deploy automatizado no GitHub Pages  

### Infraestrutura & DevOps
✅ **Docker** — Multi-stage builds otimizados para backend e frontend  
✅ **Docker Compose** — Orquestração local de todos os serviços  
✅ **Redis Pub/Sub (Upstash)** — Mensageria assíncrona para eventos de gamificação  
✅ **CI/CD com GitHub Actions** — Pipeline automatizado de build e deploy  
✅ **Render** — Backend em produção com HTTPS automático  
✅ **GitHub Pages** — Frontend estático com deploy contínuo  
✅ **Variáveis de ambiente** — Zero credenciais no código (12-Factor App)  
✅ **Traefik** — Reverse proxy e load balancer no ambiente local  

### Painel Administrativo (Streamlit + Python)
✅ Dashboard de análise de dados com visualizações interativas  
✅ Integração com a API do backend  

---

## 🔄 Atualizações Recentes

### Março/2026

- **Migrei de Apache Kafka para Redis Pub/Sub** — Substituí toda a infraestrutura de Kafka (KRaft) por Redis Pub/Sub com Upstash como provedor gerenciado. A migração simplificou a stack, reduziu custos e manteve a comunicação assíncrona para eventos de gamificação.

- **Refatorei toda a configuração de segurança (CORS/Security)** — Unifiquei a configuração CORS no Spring Security com `allowedOriginPatterns` configurável por variável de ambiente, liberação explícita de preflight `OPTIONS`, e endpoints públicos para cadastro e login acessíveis por qualquer pessoa.

- **Eliminei credenciais hardcoded do repositório** — Todas as senhas, tokens e secrets agora são lidas exclusivamente por variáveis de ambiente (`JWT_SECRET`, `MYSQLPASSWORD`, `REDIS_PASSWORD`, etc.), seguindo o princípio 12-Factor App.

- **Corrigi inconsistências do perfil de produção** — Removi dialeto PostgreSQL incorreto que estava no `application-prod.properties` (o banco é MySQL), ajustei `ddl-auto` para `update`, e eliminei `context-path` duplicado que gerava `/api/api/`.

- **Atualizei o pipeline CI/CD** — Mantive pipeline de build no GitHub Actions e deploy automático via integração nativa do Render.

---

## 🏗️ Arquitetura

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   Frontend       │     │   Backend        │     │   Streamlit      │
│   React + Vite   │────▶│   Spring Boot 3  │◀────│   Python         │
│   GitHub Pages   │     │   Render         │     │   Dashboard      │
└──────────────────┘     └────────┬─────────┘     └──────────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼             ▼             ▼
             ┌───────────┐ ┌───────────┐ ┌───────────┐
             │  Database │ │  Redis    │ │  Traefik  │
             │  Render   │ │  Upstash  │ │  Proxy    │
             └───────────┘ └───────────┘ └───────────┘
```

- **Backend (Spring Boot 3 / Java 21)** → Lógica de negócio, reputação, gamificação e blockchain
- **Redis Pub/Sub (Upstash)** → Eventos assíncronos para gamificação em tempo real
- **Frontend (React + Vite)** → Interface do usuário com SPA
- **Streamlit (Python)** → Painel administrativo e visualizações de dados
- **Database (Render)** → Persistência de dados em produção
- **Docker Compose + Traefik** → Orquestração e roteamento local
- **GitHub Actions + Render** → CI de backend e deploy automático em produção

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
| **Database (Render)** | Persistência relacional | ✅ Produção |
| **JWT + Spring Security** | Autenticação e autorização por roles | ✅ Produção |
| **React + Vite + TailwindCSS** | Frontend SPA responsivo | ✅ Produção (GitHub Pages) |
| **Swagger/OpenAPI** | Documentação automática da API | ✅ Implementado |
| **Render** | PaaS para backend em nuvem | ✅ Produção |
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
2. **Está em produção** — Backend no Render, frontend no GitHub Pages, CI/CD rodando
3. **Segue boas práticas** — 12-Factor, variáveis de ambiente, multi-stage Docker
4. **Arquitetura real** — Serviços com mensageria assíncrona, não monolito simples
5. **Completo** — Java + React + Python + DevOps + Cloud

---

## 📫 Contato

Desenvolvido por **Gabriel Cauê**  
📧 Entre em contato pelo [GitHub](https://github.com/gabrielcaue)
[Linkedin] (https://www.linkedin.com/in/gabrielcaues)

