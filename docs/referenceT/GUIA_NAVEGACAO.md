# 🗺️ Guia de Navegação Completo — Mifica Docs

Este documento centraliza todos os links da documentação do Mifica, organizados por nível, tópico e público-alvo. Use para encontrar exatamente o que precisa.

---

## 📊 Quick Links por Perfil

### Para Novos Desenvolvedores
**Seu caminho ideal (1-2 horas):**
1. [../referenceT/STACK_VISUAL_2026.md](./STACK_VISUAL_2026.md) (15 min) — Veja o mapa visual
2. [../referenceT/TECNOLOGIAS_RESUMO.md](./TECNOLOGIAS_RESUMO.md) (15 min) — Entenda o stack
3. [../packages/backend-onboarding.md](../packages/backend-onboarding.md) (30 min) — Setup e conceitos
4. [../referenceT/TESTING_QUICK_START.md](./TESTING_QUICK_START.md) (10 min) — Como testar

### Para Arquitetos / Líderes Tech
**Entender a arquitetura (1 hora):**
1. [../referenceT/STACK_VISUAL_2026.md](./STACK_VISUAL_2026.md) — Visualizar arquitetura
2. [../packages/00-INDEX.md](../packages/00-INDEX.md) — Índice SDD de pacotes
3. [../packages/backend-cdd-analysis.md](../packages/backend-cdd-analysis.md) — Análise de complexidade
4. [../packages/backend-workflows.md](../packages/backend-workflows.md) — Fluxos críticos

### Para QA / Testes
**Documentação de testes:**
1. [../referenceT/TESTING_QUICK_START.md](./TESTING_QUICK_START.md) — Guia prático rápido
2. [../packages/backend-test-architecture.md](../packages/backend-test-architecture.md) — SDD detalhado
3. [../TESTING_STRATEGY.md](../TESTING_STRATEGY.md) — Estratégia completa
4. [../packages/backend-test.md](../packages/backend-test.md) — Especificação de testes

### Para DevOps / Infra
**Documentação de infraestrutura:**
1. [../guideD.I/DEPLOYMENT_RAILWAY.md](../guideD.I/DEPLOYMENT_RAILWAY.md) — Deploy em produção
2. [../../TECHNOLOGY_INTEGRATION_MAP.md](../../TECHNOLOGY_INTEGRATION_MAP.md) — Mapa de integrações
3. [../../docker-compose.yml](../../docker-compose.yml) — Orquestração local
4. [../../docker-compose.prod.yml](../../docker-compose.prod.yml) — Orquestração produção

---

## 🏠 Índices Principais

### 📚 Documentação de Referência (referenceT/)
| Documento | Função | Para quem |
|---|---|---|
| [INDEX_COMPLETO.md](./INDEX_COMPLETO.md) | Índice completo de todos os tópicos | Iniciantes, pesquisadores |
| [STACK_VISUAL_2026.md](./STACK_VISUAL_2026.md) | Arquitetura visual com diagramas | Todos, especialmente visuais |
| [TECNOLOGIAS_RESUMO.md](./TECNOLOGIAS_RESUMO.md) | Stack completo com versões e padrões | Arquitetos, backend devs |
| [TESTING_QUICK_START.md](./TESTING_QUICK_START.md) | Atalhos e guia prático de testes | QA, devs de testes |
| [ICP_CHECKLIST.md](./ICP_CHECKLIST.md) | Checklist de complexidade percebida | Arquitetos |
| [GUIA_NAVEGACAO.md](./GUIA_NAVEGACAO.md) | Este documento — Mapa de navegação | Todos |

### 📦 Índices de Pacotes (packages/)
| Documento | Função | Escopo |
|---|---|---|
| [00-INDEX.md](../packages/00-INDEX.md) | Índice SDD de todos os pacotes | Backend, Frontend, Streamlit, Mobile |
| [backend-quick-reference.md](../packages/backend-quick-reference.md) | Cartão de referência rápida | Backend Java |
| [backend-onboarding.md](../packages/backend-onboarding.md) | Guia de início rápido | Backend Java (30 min) |
| [backend-cdd-analysis.md](../packages/backend-cdd-analysis.md) | Análise CDD/ICP de 60 classes | Backend Java (complexidade) |

### 📖 Guias Práticos (guideD.I/)
| Documento | Função | Quando usar |
|---|---|---|
| [BACKEND_GUIDE.md](../guideD.I/BACKEND_GUIDE.md) | Guia completo de backend | Desenvolvimento backend |
| [DEPLOYMENT_RAILWAY.md](../guideD.I/DEPLOYMENT_RAILWAY.md) | Deploy em Railway | Preparar produção |
| [IMPLEMENTATION_CHECKLIST.md](../guideD.I/IMPLEMENTATION_CHECKLIST.md) | Checklist de implementação | Antes de commits |
| [TESTING_OVERVIEW.md](../guideD.I/TESTING_OVERVIEW.md) | Visão geral de testes | Planejar cobertura |

---

## 🎯 Navegação por Tópico

### Backend (Java/Spring Boot)
**Setup e Aprendizado:**
- [backend-onboarding.md](../packages/backend-onboarding.md) — Setup em 30 min
- [BACKEND_GUIDE.md](../guideD.I/BACKEND_GUIDE.md) — Guia completo
- [backend-quick-reference.md](../packages/backend-quick-reference.md) — Referência rápida

**Arquitetura:**
- [backend-cdd-analysis.md](../packages/backend-cdd-analysis.md) — Análise de complexidade
- [backend-workflows.md](../packages/backend-workflows.md) — Diagramas de fluxo
- [STACK_VISUAL_2026.md](./STACK_VISUAL_2026.md) — Arquitetura visual

**Padrões de Código:**
- [backend-code-patterns.md](../packages/backend-code-patterns.md) — 10+ templates
- [backend-service.md](../packages/backend-service.md) — Especificação de serviços
- [backend-controller.md](../packages/backend-controller.md) — Especificação de controllers
- [backend-entity.md](../packages/backend-entity.md) — Modelo de dados

**Testes:**
- [TESTING_QUICK_START.md](./TESTING_QUICK_START.md) — Guia prático (10 min)
- [backend-test-architecture.md](../packages/backend-test-architecture.md) — SDD de testes
- [../TESTING_STRATEGY.md](../TESTING_STRATEGY.md) — Estratégia completa
- [backend-test.md](../packages/backend-test.md) — Especificação

**Dados e Integrações:**
- [backend-repository.md](../packages/backend-repository.md) — Acesso a dados
- [backend-redis.md](../packages/backend-redis.md) — Redis e Pub/Sub
- [backend-blockchain.md](../packages/backend-blockchain.md) — Integração blockchain
- [backend-config.md](../packages/backend-config.md) — Configuração

### Frontend (React)
- [frontend-components.md](../packages/frontend-components.md) — Componentes
- [frontend-pages.md](../packages/frontend-pages.md) — Páginas e rotas
- [frontend-context.md](../packages/frontend-context.md) — State management
- [frontend-services.md](../packages/frontend-services.md) — Serviços HTTP

### Streamlit (Analytics)
- [streamlit-app.md](../packages/streamlit-app.md) — App principal
- [streamlit-components.md](../packages/streamlit-components.md) — Componentes
- [streamlit-data.md](../packages/streamlit-data.md) — Análise de dados

### Mobile (Flutter)
- [flutter-config.md](../packages/flutter-config.md) — Configuração

### Infraestrutura
- [DEPLOYMENT_RAILWAY.md](../guideD.I/DEPLOYMENT_RAILWAY.md) — Deploy Railway
- [../../docker-compose.yml](../../docker-compose.yml) — Orquestração dev
- [../../docker-compose.prod.yml](../../docker-compose.prod.yml) — Orquestração prod

### Qualidade e Segurança
- [../GUARDRAILS.md](../GUARDRAILS.md) — Guardrails de segurança
- [../GUARDRAILS_CHECKLIST.md](../GUARDRAILS_CHECKLIST.md) — Checklist de segurança
- [backend-cdd-analysis.md](../packages/backend-cdd-analysis.md) — Análise de complexidade (evita débito técnico)
- [TECNOLOGIAS_RESUMO.md](./TECNOLOGIAS_RESUMO.md) — Stack segura (versões estáveis)

---

## 🔍 Busca Rápida

### "Como fazer X?"

| Pergunta | Resposta |
|---|---|
| Como fazer setup do backend? | [backend-onboarding.md](../packages/backend-onboarding.md#setup) |
| Como criar um novo endpoint? | [BACKEND_GUIDE.md](../guideD.I/BACKEND_GUIDE.md#novo-endpoint) |
| Como criar um novo serviço? | [backend-code-patterns.md](../packages/backend-code-patterns.md#novo-service) |
| Como testar uma API? | [TESTING_QUICK_START.md](./TESTING_QUICK_START.md#api-test) |
| Como fazer teste unitário? | [backend-test-architecture.md](../packages/backend-test-architecture.md#unit-test) |
| Como fazer teste de integração? | [backend-test-architecture.md](../packages/backend-test-architecture.md#integration-test) |
| Como fazer deploy? | [DEPLOYMENT_RAILWAY.md](../guideD.I/DEPLOYMENT_RAILWAY.md) |
| Como monitorar em produção? | [../../README.md#observabilidade-prometheus--grafana](../../README.md#observabilidade-prometheus--grafana) |
| Como entender uma classe complexa? | [backend-cdd-analysis.md](../packages/backend-cdd-analysis.md) |

### "Preciso aprender sobre X"

| Tópico | Documentação |
|---|---|
| JWT e Autenticação | [backend-config.md](../packages/backend-config.md#autenticação) |
| Redis e Pub/Sub | [backend-redis.md](../packages/backend-redis.md) |
| Blockchain | [backend-blockchain.md](../packages/backend-blockchain.md) |
| Gamificação | [backend-service.md](../packages/backend-service.md#gamificação) |
| Reputação | [backend-service.md](../packages/backend-service.md#reputação) |
| Banco de dados | [backend-entity.md](../packages/backend-entity.md) |
| Observabilidade | [TECNOLOGIAS_RESUMO.md](./TECNOLOGIAS_RESUMO.md#6-observabilidade) |

---

## 📍 Localização dos Arquivos

```
/Users/user/mifica/
├── README.md (start here)
├── TECHNOLOGY_INTEGRATION_MAP.md
├── ESTRUTURA_PROJETO.md
│
├── docs/
│   ├── guideD.I/
│   │   ├── BACKEND_GUIDE.md ⭐
│   │   ├── DEPLOYMENT_RAILWAY.md
│   │   ├── IMPLEMENTATION_CHECKLIST.md
│   │   └── TESTING_OVERVIEW.md
│   │
│   ├── referenceT/ (você está aqui)
│   │   ├── STACK_VISUAL_2026.md
│   │   ├── TECNOLOGIAS_RESUMO.md
│   │   ├── TESTING_QUICK_START.md
│   │   ├── INDEX_COMPLETO.md
│   │   ├── ICP_CHECKLIST.md
│   │   └── GUIA_NAVEGACAO.md (este arquivo)
│   │
│   ├── packages/
│   │   ├── 00-INDEX.md ⭐
│   │   ├── backend-*.md (15 arquivos)
│   │   ├── frontend-*.md (8 arquivos)
│   │   ├── streamlit-*.md (7 arquivos)
│   │   └── ... (e mais)
│   │
│   ├── GUARDRAILS.md
│   ├── GUARDRAILS_CHECKLIST.md
│   ├── GUARDRAILS_RESUMO.md
│   ├── TESTING_STRATEGY.md
│   ├── TESTING_QUICK_REFERENCE.md
│   ├── technology-integration.bpmn
│   └── technology-integration.svg
│
├── mifica-backend/
├── mifica-frontend/
├── mifica-streamlit/
└── mifica-flutter/
```

---

## 💡 Dicas de Navegação

1. **Para quem está começando:** Comece com [STACK_VISUAL_2026.md](./STACK_VISUAL_2026.md)
2. **Para encontrar uma classe Java específica:** Use [backend-cdd-analysis.md](../packages/backend-cdd-analysis.md)
3. **Para entender o fluxo de autenticação:** Veja [backend-workflows.md](../packages/backend-workflows.md#autenticação)
4. **Para saber como testar:** Abra [TESTING_QUICK_START.md](./TESTING_QUICK_START.md)
5. **Quando em dúvida, use o índice completo:** [00-INDEX.md](../packages/00-INDEX.md)

---

## 🔗 Referências Externas

- **BPMN Visual:** [docs/technology-integration.bpmn](../../docs/technology-integration.bpmn) (abrir no Camunda Modeler)
- **GitHub:** [github.com/gabrielcaue/mifica](https://github.com/gabrielcaue/mifica)
- **Swagger API:** http://localhost:8080/swagger-ui.html (local) ou Railway URL (prod)
- **Grafana:** http://localhost:3000 (local) ou Railway URL (prod)

---

**Última atualização:** 7 de junho de 2026
