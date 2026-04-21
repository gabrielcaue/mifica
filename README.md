# Mifica — Plataforma de Reputação, Gamificação e Blockchain

Plataforma full-stack com arquitetura modular, focada em backend robusto, segurança e operação em produção.

## Status

- Em manutenção evolutiva
- Frontend publicado no GitHub Pages
- Backend com perfil de produção no Railway

## Acesso e Documentação

| Recurso | Link |
|---|---|
| Frontend (GitHub Pages) | [gabrielcaue.github.io/mifica](https://gabrielcaue.github.io/mifica/) |
| Repositório | [github.com/gabrielcaue/mifica](https://github.com/gabrielcaue/mifica) |
| Índice técnico | [docs/packages/00-INDEX.md](docs/packages/00-INDEX.md) |
| Resumo de tecnologias | [TECNOLOGIAS_RESUMO.md](TECNOLOGIAS_RESUMO.md) |

## Visão Geral da Solução

- **Backend (Spring Boot 3 + Java 21):** API REST, autenticação JWT, regras de reputação e gamificação, integração Redis Pub/Sub e blockchain.
- **Frontend (React + Vite + Tailwind):** SPA com login, cadastro, dashboard e rotas protegidas.
- **Streamlit (Python):** painel analítico para acompanhamento de dados da plataforma.
- **Infraestrutura:** Docker/Docker Compose; Traefik no ambiente local e Railway no ambiente de produção.

## Destaques Técnicos

- Segurança centralizada com Spring Security + JWT + CORS por variável de ambiente.
- Persistência com Spring Data JPA/Hibernate e MySQL.
- Comunicação assíncrona com Redis Pub/Sub (Upstash em produção).
- Migrações versionadas com Flyway.
- Auditoria de entidades com Hibernate Envers.
- Documentação de API com OpenAPI/Swagger.
- Observabilidade base com Actuator.
- Organização em camadas (`controller`, `service`, `repository`, `dto`, `config`, `util`).
- Princípios SOLID aplicados na arquitetura (responsabilidade única, aberto/fechado, inversão de dependência).

## Atualizações Recentes (2026)

### Backend
- Padronização da segurança HTTP e CORS no `SecurityConfig`.
- Consolidação de variáveis sensíveis em ambiente (12-Factor App).
- Ajustes de perfil de produção para MySQL/Redis.
- Remoção de funcionalidades de e-mail por enquanto (será adicionado futuramente com provedor integrado).

### Frontend
- Fluxos de autenticação otimizados e consolidados.

### Engenharia e Documentação
- Aplicação de CDD/ICP em classes críticas do backend.
- Estruturação de documentação técnica em pacotes (SDD) em [docs/packages/](docs/packages/).

## Arquitetura (alto nível)

```text
Frontend (React/Vite)  --->  Backend API (Spring Boot)  --->  MySQL
                                    |                        
                                    +--> Redis Pub/Sub (Upstash)
                                    +--> Integrações blockchain (Web3j)
                                    +--> Streamlit (consumo analítico)
```

## Estrutura do Repositório

```text
mifica/
├── mifica-backend/
├── mifica-frontend/
├── mifica-streamlit/
├── mifica-flutter/
├── docs/packages/
├── k8s/
├── docker-compose.yml
└── docker-compose.prod.yml
```

## Execução Local

1. Clone o repositório
2. Suba o ambiente com Docker Compose

```bash
git clone https://github.com/gabrielcaue/mifica.git
cd mifica
./start-local.sh
```

Se quiser incluir o SonarQube local na mesma máquina, rode:

```bash
./start-local.sh --with-sonar
```

Serviços locais esperados:

| Serviço | URL |
|---|---|
| Backend | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| Streamlit | http://localhost:8501 |
| Swagger | http://localhost:8080/swagger-ui.html |

## Fluxo de Produção

- Frontend: GitHub Pages em [gabrielcaue.github.io/mifica](https://gabrielcaue.github.io/mifica/)
- Backend: Railway
- Prometheus: serviço separado no Railway para coletar métricas do backend
- Grafana: serviço separado no Railway, acessado pela URL pública gerada pela plataforma
- SonarQube: apenas local, via `docker compose --profile quality up -d sonarqube-db sonarqube`

Como não há domínio próprio, o Grafana no Railway deve ser acessado pela URL do serviço, algo como `https://<seu-serviço>.up.railway.app`.
O Grafana precisa apontar para o Prometheus do Railway como datasource.
O Prometheus do Railway precisa estar configurado com o endereço do backend do Railway no scrape target.

Variáveis úteis no Railway:

- `BACKEND_TARGET=<servico-backend>.railway.internal:8080`
- `BACKEND_SCHEME=http`
- `PROMETHEUS_URL=http://<servico-prometheus>.railway.internal:9090`

## Qualidade de Código (SonarQube Community)

O projeto agora possui configuração para SonarQube local (Community Edition) usando Docker.

### Subir SonarQube

```bash
docker compose --profile quality up -d sonarqube-db sonarqube
```

- URL: http://localhost:9000
- Login inicial: `admin` / `admin` (será solicitada troca de senha no primeiro acesso)

### Rodar análise do projeto

1. Gere um token no SonarQube (My Account > Security)
2. Exporte o token no terminal:

```bash
export SONAR_TOKEN="seu_token"
```

3. Rode o scanner em container:

```bash
docker run --rm \
    --network mifica_default \
    -e SONAR_HOST_URL="http://sonarqube:9000" \
    -e SONAR_TOKEN="$SONAR_TOKEN" \
    -v "$PWD:/usr/src" \
    sonarsource/sonar-scanner-cli
```

> A configuração de análise está no arquivo `sonar-project.properties` na raiz do repositório.

### Produção

- O Grafana deve subir como serviço separado no Railway e apontar para a fonte de métricas da produção.
- O SonarQube permanece fora da produção e roda só localmente.
- Se você rodar SonarQube em Linux, lembre de ajustar `vm.max_map_count`.

## Observabilidade (Prometheus + Grafana)

Foi adicionada uma stack OSS muito usada em empresas:

- **Prometheus** para coleta de métricas
- **Grafana** para dashboards
- **cAdvisor** para métricas de containers

### Subir stack de observabilidade

```bash
docker compose --profile observability up -d
```

URLs:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (login padrão: `admin` / `admin`)
- cAdvisor: http://localhost:8088

O backend expõe métricas em `http://localhost:8080/actuator/prometheus`.

O stack usa:

- `monitoring/prometheus/prometheus.yml` para os scrape jobs
- `monitoring/grafana/provisioning/datasources/datasource.yml` para conectar o Grafana ao Prometheus automaticamente
- `monitoring/grafana/provisioning/dashboards/dashboard.yml` para provisionar dashboards
- `monitoring/grafana/dashboards/mifica-observability-overview.json` com dashboard inicial (latência, erro, throughput e recursos)

Se quiser avaliar a coleta completa, suba também o backend no mesmo `docker compose` para o Prometheus enxergar o endpoint do Actuator.

## Observações da Engenharia

O projeto **não é fogo de artifício** — estamos construindo com base sólida, escalável e pronta para produção. A arquitetura segue princípios maduros (SOLID, 12-Factor App) e não é apenas um **MVP para gaveta**. 

Temos **boas fundações**, então evitamos a situação de **farmácia**, onde tudo vai virar um remendo (spaghetti code). A governança de dados com Flyway e Envers garante que mudanças no schema e histórico de entidades sejam rastreáveis, não virando um **caos de migrações**.

O stack está **bem amarrado** (tightly integrated) com Docker, CI/CD no GitHub Actions e observabilidade via Prometheus/Grafana. Isso nos deixa com um **bom ground truth** sobre o que está acontecendo em produção, não no escuro.

### Pontos de Atenção para com o Projeto

- **Fogo de artifício:** solução bonita na demo, mas sem sustentação no mundo real.
- **Farmácia:** ambiente espelhado para testar com segurança, sem afetar produção.
- **Churrasco de bug:** quando os erros começam pequenos e viram bagunça generalizada.
- **Frankenstein:** stack colada de qualquer jeito, sem integração de verdade.
- **Ground truth:** a visão real do sistema via métricas, logs e dashboards.

## Roadmap Técnico

- Keycloak/OIDC para evolução de identidade e autenticação.
- Busca avançada e analytics (Elastic/OpenSearch).
- Evolução de resiliência distribuída (circuit breaker e políticas de tráfego).
- Datamesh para descentralização de governança de dados e domínios autônomos.

## Contato

Desenvolvido por Gabriel Cauê.

- GitHub: [github.com/gabrielcaue](https://github.com/gabrielcaue)
- LinkedIn: [linkedin.com/in/gabrielcaues](https://www.linkedin.com/in/gabrielcaues)

