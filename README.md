# Mifica — Plataforma de Reputação, Gamificação e Blockchain

Plataforma full-stack com arquitetura modular, focada em backend robusto, segurança e operação em produção.

## Status

- Em manutenção evolutiva
- Frontend publicado no GitHub Pages
- Backend com perfil de produção e deploy via Docker Compose

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
- **Infraestrutura:** Docker/Docker Compose; Traefik no ambiente local e Nginx no ambiente de produção (EC2 + HTTPS).

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
├── ec2/
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
./start-dev.sh
```

Serviços locais esperados:

| Serviço | URL |
|---|---|
| Backend | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| Streamlit | http://localhost:8501 |
| Swagger | http://localhost:8080/swagger-ui.html |

## Roadmap Técnico

- Prometheus + Grafana para observabilidade operacional.
- Keycloak/OIDC para evolução de identidade e autenticação.
- Busca avançada e analytics (Elastic/OpenSearch).
- Evolução de resiliência distribuída (circuit breaker e políticas de tráfego).

## Contato

Desenvolvido por Gabriel Cauê.

- GitHub: [github.com/gabrielcaue](https://github.com/gabrielcaue)
- LinkedIn: [linkedin.com/in/gabrielcaues](https://www.linkedin.com/in/gabrielcaues)

