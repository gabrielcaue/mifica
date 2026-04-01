# Tecnologias do Mifica — Resumo Atualizado

## 1) Backend (foco principal)

### Linguagem e Base

| Tecnologia | Onde está | Função no projeto |
|---|---|---|
| Java 21 | backend | Linguagem principal da API |
| Spring Boot 3.5.6 | backend | Bootstrap da aplicação, configuração e runtime |
| Maven | backend | Build, gerenciamento de dependências e empacotamento |

### API, Segurança e Autenticação

| Tecnologia | Onde está | Função no projeto |
|---|---|---|
| spring-boot-starter-web | backend | Endpoints REST |
| Spring Security | backend | Política de autenticação/autorização |
| JWT (jjwt 0.11.5) | backend | Tokens stateless para autenticação |
| BCryptPasswordEncoder | backend | Hash de senha e validação segura |
| Filtro JWT custom (`JwtFiltro`) | backend | Interceptação de requisições protegidas |
| CORS centralizado (`SecurityConfig`) | backend | Controle de origens por variável de ambiente |

### Persistência, Banco e Governança de Dados

| Tecnologia | Onde está | Função no projeto |
|---|---|---|
| Spring Data JPA | backend | Repositórios e abstração de persistência |
| Hibernate ORM | backend | Implementação JPA |
| Hibernate Envers | backend | Auditoria de entidades (`@Audited`) |
| Flyway + Flyway MySQL | backend | Versionamento de schema e migrações |
| MySQL Connector/J | backend | Conexão com banco principal |
| PostgreSQL driver | backend | Compatibilidade de ambientes legados/alternativos |
| H2 (runtime) | backend | Banco em memória para cenários locais/testes |

### Assíncrono, Integrações e Observabilidade

| Tecnologia | Onde está | Função no projeto |
|---|---|---|
| Redis Pub/Sub | backend | Processamento assíncrono de eventos de gamificação |
| Spring Data Redis | backend | Integração com Redis e listener container |
| Upstash (produção) | backend/infra | Redis gerenciado em ambiente produtivo |
| OpenAPI/Swagger (springdoc 2.5.0) | backend | Documentação interativa da API |
| Spring Boot Actuator | backend | Endpoints de health/info |
| Web3j 4.9.8 | backend | Integração com recursos blockchain |
| Hibernate Validator + Jakarta EL | backend | Validação de payloads e regras de entrada |

## 2) Frontend Web

| Tecnologia | Função no projeto |
|---|---|
| React 18 | UI da SPA |
| Vite 7 | Build e servidor de desenvolvimento |
| TailwindCSS + PostCSS + Autoprefixer | Estilização utilitária e pipeline CSS |
| React Router DOM 6 | Rotas e páginas protegidas |
| Axios | Cliente HTTP para integração com backend |
| jwt-decode | Decodificação de token no cliente |
| gh-pages | Deploy da SPA no GitHub Pages |

## 3) Streamlit e Analytics

| Tecnologia | Função no projeto |
|---|---|
| Python + Streamlit | Painel analítico e operacional |
| requests | Consumo de API |
| PyJWT | Manipulação de token no painel |
| pandas + numpy | Tratamento e análise de dados |
| plotly | Visualizações interativas |
| Pillow | Tratamento de imagens no dashboard |

## 4) Mobile (base)

| Tecnologia | Função no projeto |
|---|---|
| Flutter + Dart | Base de cliente mobile |
| Flutter Secure Storage | Armazenamento seguro de token |

## 5) Infraestrutura e Entrega

| Tecnologia | Função no projeto |
|---|---|
| Docker | Containerização dos serviços |
| Docker Compose | Orquestração local e produção |
| Traefik | Proxy reverso no ambiente local |
| Nginx | Proxy reverso no ambiente de produção |
| AWS EC2 | Hospedagem da stack produtiva |
| GitHub Actions | Pipeline de build/test/deploy |
| GitHub Pages | Entrega do frontend estático |
| Variáveis de ambiente (12-Factor) | Gestão segura de configuração |

---

## Tecnologias recentemente evidenciadas (já em uso)

- **Flyway** e **Envers** presentes para governança de dados (migração + auditoria).
- **Redis Pub/Sub com listener container** consolidado para eventos assíncronos.
- **CORS por `allowed-origin-patterns`** e filtros JWT centralizados no backend.

---

## Princípios de Design e Arquitetura

### SOLID

O projeto aplica os cinco princípios SOLID em sua arquitetura:

- **S (Single Responsibility):** Cada classe tem uma única responsabilidade (Controllers lidam com HTTP, Services com lógica, Repositories com acesso a dados).
- **O (Open/Closed):** Extensível sem modificação (ex.: novos tipos de validação, novos endpoints).
- **L (Liskov Substitution):** Interfaces Spring (JpaRepository, Service abstracts) permitem substituição de implementações.
- **I (Interface Segregation):** Interfaces específicas por domínio (`UsuarioRepository`, `TransacaoRepository`), não gigantes.
- **D (Dependency Inversion):** Injeção de dependência via Spring; classes dependem de abstrações (interfaces), não de implementações concretas.

---

## Observação de escopo

Este resumo prioriza tecnologias **implementadas e detectáveis no código e configuração atual** do repositório, com ênfase no backend.
