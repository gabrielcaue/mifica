# 📂 Estrutura do Projeto Mifica

## 🏗️ Raiz (Apenas Essencial)

```
/mifica
├── README.md                          ⭐ COMECE AQUI
├── docker-compose.yml                 ✅ Local dev
├── docker-compose.prod.yml            ✅ Produção
├── Procfile                           ✅ Railway config
├── sonar-project.properties           ✅ SonarQube
├── nginx.prod.conf                    ✅ Nginx config
│
├── 📜 Scripts Essenciais
│   ├── start-dev.sh                   → Iniciar ambiente local
│   ├── deploy-prod.sh                 → Deploy em produção
│   ├── health-check.sh                → Verificar saúde
│   ├── logs.sh                        → Ver logs
│   └── fix-java-vscode.sh             → Fixar Java no VSCode
│
└── 📁 Pastas principais (sem clutter de .md)
```

---

## 📚 Documentação Organizada (Hierárquica)

### 1️⃣ **Nível 1: Índices (Para navegar)**
```
/docs/reference/INDEX_COMPLETO.md    → 🗺️ Mapa completo do projeto
/docs/packages/00-INDEX.md            → 📦 Especificações de pacotes
```

### 2️⃣ **Nível 2: Guias (Como fazer / Começar)**
```
/docs/guide/
├── BACKEND_GUIDE.md                   → 🎯 Backend (CDD/ICP, onboarding, padrões)
├── DEPLOYMENT_RAILWAY.md              → 🚀 Deploy em Railway
├── IMPLEMENTATION_CHECKLIST.md        → ✅ Checklist de tarefas
└── TESTING_OVERVIEW.md                → 🧪 Visão geral de testes
```

### 3️⃣ **Nível 3: Referência (Consulta técnica)**
```
/docs/reference/
├── TECNOLOGIAS_RESUMO.md              → 🔧 Stack de tecnologias
├── STACK_VISUAL_2026.md               → 📊 Arquitetura com diagramas
├── TESTING_QUICK_START.md             → ⚡ Atalhos de testes
└── ICP_CHECKLIST.md                   → 📈 Índice de complexidade
```

### 4️⃣ **Nível 4: Especificações de Arquitetura (SDD)**
```
/docs/packages/
├── 00-INDEX.md                        → Índice de pacotes
├── backend-quick-reference.md         → Cartão rápido
├── backend-test-architecture.md       → SDD de testes
├── backend-*.md                       → Especificações por camada
└── frontend-*.md                      → Especificações frontend
```

### 5️⃣ **Nível 5: Testes (Documentação específica)**
```
/docs/TESTING_STRATEGY.md              → Estratégia detalhada
/docs/TESTING_QUICK_REFERENCE.md       → Resumo rápido
```

---

## 🏢 Código Fonte

```
/mifica-backend/                       → Spring Boot (Java 17)
/mifica-frontend/                      → React + Vite
/mifica-flutter/                       → Flutter (Mobile)
/mifica-streamlit/                     → Streamlit (Analytics)
```

---

## 🔧 Infraestrutura

```
/k8s/                                  → Kubernetes manifests
/docker-compose files                  → Orquestração local/prod
/nginx.prod.conf                       → Proxy reverso (produção)
```

---

## 📊 Observabilidade

```
/prometheus/                           → Configuração Prometheus
/grafana/                              → Dashboards Grafana
/monitoring/                           → Scripts de monitoramento
```

---

## 🔐 Segurança

```
/certbot/                              → SSL/TLS certificates
.env.example                           → Template de variáveis
```

---

## 🛠️ Ferramentas

```
/tools/spec-kit/                       → SpecKit (documentação)
/ec2/                                  → Scripts EC2
```

---

## 📋 Como Navegar

### 🆕 Novo no projeto?
1. Leia [README.md](README.md)
2. Acesse [docs/reference/INDEX_COMPLETO.md](docs/reference/INDEX_COMPLETO.md)
3. Escolha seu path: Backend → [docs/guide/BACKEND_GUIDE.md](docs/guide/BACKEND_GUIDE.md)

### 👨‍💼 Arquiteto / Líder?
1. [docs/packages/00-INDEX.md](docs/packages/00-INDEX.md) — Visão de arquitetura
2. [docs/reference/STACK_VISUAL_2026.md](docs/reference/STACK_VISUAL_2026.md) — Diagramas

### 👨‍💻 Desenvolvedor pronto para coding?
1. [docs/guide/BACKEND_GUIDE.md](docs/guide/BACKEND_GUIDE.md) — Setup
2. [docs/reference/TESTING_QUICK_START.md](docs/reference/TESTING_QUICK_START.md) — Testes

### 🧪 Foco em Testes?
1. [docs/reference/TESTING_QUICK_START.md](docs/reference/TESTING_QUICK_START.md) — Rápido
2. [docs/packages/backend-test-architecture.md](docs/packages/backend-test-architecture.md) — Detalhado

---

## ✨ Benefícios da Organização

✅ **Raiz limpa**: Apenas arquivos que você precisa diariamente  
✅ **Hierarquia clara**: 5 níveis de detalhe (do geral ao específico)  
✅ **Fácil encontrar**: Use índices para navegar  
✅ **Escalável**: Novas docs seguem o padrão estabelecido  
✅ **Profissional**: Estrutura pronta para documentação empresarial  

---

**Última atualização:** 16 de maio de 2026
