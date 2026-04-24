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

### Assíncrono, Integrações e Telemetria

| Tecnologia | Onde está | Função no projeto |
|---|---|---|
| Redis Pub/Sub | backend | Processamento assíncrono de eventos de gamificação e sinalização entre módulos |
| Spring Data Redis | backend | Integração com Redis e listener container para consumo dos eventos |
| Upstash (produção) | backend/infra | Redis gerenciado para suportar filas leves e eventos em produção |
| OpenAPI/Swagger (springdoc 2.5.0) | backend | Documentação da API para consumo do frontend e manutenção da integração |
| Spring Boot Actuator | backend | Exposição de health, metrics e readiness para a stack de observabilidade |
| Web3j 4.9.8 | backend | Integração com recursos blockchain e fluxo de transações da plataforma |
| Hibernate Validator + Jakarta EL | backend | Validação de payloads e regras de entrada antes de persistir ou publicar eventos |

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
| Nginx | Proxy reverso no ambiente de produção local |
| Railway | Hospedagem do backend em produção |
| GitHub Actions | Pipeline de build/test/deploy |
| GitHub Pages | Entrega do frontend estático |
| Variáveis de ambiente (12-Factor) | Gestão segura de configuração |

## 6) Observabilidade, Qualidade e Análise

| Tecnologia | Função no projeto |
|---|---|
| Prometheus | Coleta central das métricas expostas pelo backend e pelo ambiente de containers |
| Grafana | Painéis para acompanhar latência, taxa de erro, throughput e uso de recursos do projeto |
| cAdvisor | Base de métricas de containers que alimenta os dashboards operacionais |
| SonarQube Community | Qualidade de código do backend Java, apoiando revisão técnica e redução de dívida |
| Actuator | Ponto de integração do backend com a telemetria e health checks |

### Como essas peças trabalham juntas

- O backend publica saúde e métricas via `Actuator`.
- O `Prometheus` coleta esses dados e também lê os contêineres via `cAdvisor`.
- O `Grafana` consolida tudo em painéis para o time enxergar tendência e problema real.
- O `SonarQube` entra no fluxo de qualidade para evitar que código ruim vire incidente depois.

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

---

## Roadmap Tecnológico

- **Keycloak/OIDC:** Evolução de identidade federada e autenticação centralizada.
- **Busca avançada e analytics:** Elastic/OpenSearch para full-text search e insights de dados.
- **Resiliência distribuída:** Circuit breakers, retry policies e rate limiting para tráfego robusto.
- **Datamesh:** Descentralização de governança de dados, transformando silos em domínios autônomos com API-first data products. Cada domínio (gamificação, reputação, blockchain, transações) como um produtor de dados com catálogo e contratos bem definidos.

---

## Ambiente de Dev como "Farmácia"

No contexto do Mifica, a ideia de **farmácia** é positiva: é um ambiente de dev/homologação que espelha o negócio para testar sem colocar a operação real em risco.

### Quando isso é bom no projeto:
- **Validação realista:** simula fluxos de autenticação, gamificação, blockchain, reputação e integrações sem mexer no ambiente vivo.
- **Segurança para evolução:** permite testar features novas sem tocar dados reais de produção ou quebrar atendimento.
- **Homologação e treinamento:** ajuda a equipe a validar o comportamento da plataforma antes de liberar mudanças.
- **Integração de verdade:** permite testar Redis, observabilidade, frontend e backend com o mesmo contrato do ambiente final.

### Boas práticas aplicadas ao Mifica:
- **Dados fictícios ou anonimizados:** nada de CPF, pacientes ou credenciais reais no dev.
- **Segregação clara:** `docker-compose.yml` para dev; `docker-compose.prod.yml`, Railway e stack de observabilidade para produção.
- **Migração previsível:** Flyway mantém o schema consistente entre ambientes.
- **Ambiente resetável:** o dev deve poder ser recriado do zero com migrations e seed controlado.

---

## Pontos de Atenção para com o Projeto

- **Não é fogo de artifício:** o projeto não é só brilho de demo; tem base para crescer com segurança e manutenção.
- **Farmácia boa:** ambiente de dev que espelha o negócio e protege produção, em vez de virar gambiarra.
- **Sem churrasco de bug:** evitamos acumular problema escondido que só estoura depois do deploy.
- **Bem amarrado:** backend, frontend, observabilidade e CI/CD conversam sem virar um Frankenstein.
- **Ground truth:** a combinação de métricas, logs e dashboards mostra o que realmente acontece no sistema.
- **Bronze/Gold:** dados e contratos organizados por nível de confiança e uso no pipeline.
- **Observabilidade de verdade:** não é só ver que caiu; é conseguir entender onde, quando e por quê.
