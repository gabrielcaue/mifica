# Índice SDD dos pacotes

Este diretório está padronizado em **SDD (Specification-Driven Development)**.

Padrão adotado em cada arquivo:
1. Objetivo do pacote
2. Escopo (inclui / não inclui)
3. Contratos e interfaces
4. Regras e invariantes
5. Critérios de aceitação (testáveis)
6. Dependências e integrações
7. Riscos e trade-offs

## Backend (Java)

### 📋 Documentação de Arquitetura (SDD/SpecKit)
**Para novos desenvolvedores - COMECE AQUI:**
- [backend-quick-reference.md](backend-quick-reference.md) - ⚡ **Cartão de referência rápida** (comandos, stack, classes críticas)
- [backend-onboarding.md](backend-onboarding.md) - 🚀 Guia de início rápido (setup, conceitos, primeiras tarefas)
- [backend-cdd-analysis.md](backend-cdd-analysis.md) - 📊 Análise CDD/ICP de 60 classes (complexidade percebida)
- [backend-code-patterns.md](backend-code-patterns.md) - 🔨 Padrões de código e templates reutilizáveis
- [backend-workflows.md](backend-workflows.md) - 🔄 Diagramas de fluxo (autenticação, Pub/Sub, blockchain)

### 📦 Especificações de Pacotes (SDD)
- [backend-app](backend-app.md)
- [backend-blockchain](backend-blockchain.md)
- [backend-config](backend-config.md)
- [backend-controller](backend-controller.md)
- [backend-dto](backend-dto.md)
- [backend-entity](backend-entity.md)
- [backend-filter](backend-filter.md)
- [backend-redis](backend-redis.md)
- [backend-repository](backend-repository.md)
- [backend-service](backend-service.md)
- [backend-util](backend-util.md)
- [backend-test](backend-test.md)

### Cobertura backend
- **Arquitetura**: Todos os 60+ classes Java analisadas em `src/main/java/com/mifica/*`
- **Padrões**: 10+ templates de código para extensão futura
- **Workflows**: 7 fluxos principais documentados com diagramas ASCII
- **Onboarding**: Guia estruturado para novos desenvolvedores em 30 minutos

## Frontend (React)
- [frontend-assets](frontend-assets.md)
- [frontend-components](frontend-components.md)
- [frontend-context](frontend-context.md)
- [frontend-hooks](frontend-hooks.md)
- [frontend-pages](frontend-pages.md)
- [frontend-routes](frontend-routes.md)
- [frontend-services](frontend-services.md)
- [frontend-styles](frontend-styles.md)
- [frontend-utils](frontend-utils.md)

## Streamlit (Python)
- [streamlit-app](streamlit-app.md)
- [streamlit-assets](streamlit-assets.md)
- [streamlit-config](streamlit-config.md)
- [streamlit-components](streamlit-components.md)
- [streamlit-data](streamlit-data.md)
- [streamlit-services](streamlit-services.md)
- [streamlit-utils](streamlit-utils.md)

## Tooling / SpecKit
- [tools-spec-kit](tools-spec-kit.md)

## Flutter
- [flutter-config](flutter-config.md)
