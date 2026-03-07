# ⚙️ Mifica Backend — Spring Boot 3 + Java 21

> ⚠️ **Status: Em manutenção** — Refatoração de infraestrutura e segurança em andamento.

API REST do projeto Mifica. Responsável por toda a lógica de reputação, gamificação, autenticação JWT e integração com Kafka.

## Stack

- **Java 21** + **Spring Boot 3.5**
- **Spring Security** + JWT customizado
- **Spring Data JPA** + Hibernate + MySQL
- **Apache Kafka** (producer/consumer para gamificação)
- **Swagger/OpenAPI** para documentação
- **Docker** com multi-stage build
- **Railway** para deploy em produção

## Variáveis de Ambiente (obrigatórias)

```env
JWT_SECRET=...
ADMIN_PASSWORD=...
ADMIN_KAFKA_PASSWORD=...
MYSQLHOST=...
MYSQLPORT=...
MYSQLDATABASE=...
MYSQLUSER=...
MYSQLPASSWORD=...
KAFKA_BOOTSTRAP_SERVERS=...
KAFKA_SECURITY_PROTOCOL=...
KAFKA_SASL_MECHANISM=...
KAFKA_USERNAME=...
KAFKA_PASSWORD=...
CORS_ALLOWED_ORIGIN_PATTERNS=*
```

> Zero credenciais no código. Tudo por variáveis de ambiente (12-Factor App).

## Páginas do Frontend (GitHub Pages)

> **Base:** `https://gabrielcaue.github.io/mifica/`

| Página | URL | Acesso |
|---|---|---|
| Login | [/#/login](https://gabrielcaue.github.io/mifica/#/login) | Público |
| Cadastro | [/#/cadastro](https://gabrielcaue.github.io/mifica/#/cadastro) | Público |
| Cadastro Admin | [/#/cadastro-admin](https://gabrielcaue.github.io/mifica/#/cadastro-admin) | 🔒 Via senha de acesso |
| Dashboard | [/#/dashboard](https://gabrielcaue.github.io/mifica/#/dashboard) | 🔒 Autenticado |
| Perfil | [/#/perfil](https://gabrielcaue.github.io/mifica/#/perfil) | 🔒 Autenticado |
| Configurações | [/#/configuracoes](https://gabrielcaue.github.io/mifica/#/configuracoes) | 🔒 Autenticado |
| Painel Admin | [/#/admin](https://gabrielcaue.github.io/mifica/#/admin) | 🔒 ADMIN only |

## Endpoints da API (Backend — Railway)

> **Base:** `https://mifica-production.up.railway.app`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/api/usuarios/cadastro` | Cadastro de usuário | Público |
| POST | `/api/usuarios/login` | Login (retorna JWT) | Público |
| GET | `/api/blockchain/**` | Consulta blockchain | Público |
| GET | `/actuator/**` | Health check / Actuator | Público |
| GET | `/swagger-ui/index.html` | Documentação interativa da API | Público |
| GET | `/v3/api-docs` | OpenAPI spec (JSON) | Público |
| GET | `/api/usuarios/**` | CRUD de usuários | 🔒 USER / ADMIN |
| POST | `/api/transacoes/**` | Transações | 🔒 Autenticado |
| POST | `/api/contratos/**` | Contratos | 🔒 Autenticado |
| POST | `/api/desafios/**` | Desafios | 🔒 Autenticado |
| POST | `/api/admin/**` | Operações administrativas | 🔒 ADMIN only |

## Como Rodar

```bash
# Via Docker Compose (na raiz do projeto)
./start-dev.sh

# Ou via Maven
./mvnw spring-boot:run
```

Veja o [README principal](../README.md) e o [Guia de Deploy](../DEPLOYMENT_RAILWAY.md) para mais detalhes.
