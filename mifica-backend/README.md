# ⚙️ Mifica Backend — Spring Boot 3 + Java 21

> ✅ **Status: Online** — Infraestrutura migrada para Redis Pub/Sub (Upstash).

API REST do projeto Mifica. Responsável por toda a lógica de reputação, gamificação, autenticação JWT e integração com Redis Pub/Sub.

## Stack

- **Java 21** + **Spring Boot 3.5**
- **Spring Security** + JWT customizado
- **Spring Data JPA** + Hibernate + MySQL
- **Flyway** para migrations versionadas de banco
- **Hibernate Envers** para auditoria de entidades críticas
- **Redis Pub/Sub** (publisher/subscriber para gamificação via Upstash)
- **Swagger/OpenAPI** para documentação
- **Docker** com multi-stage build
- **Cloud provider** para deploy em produção

## Variáveis de Ambiente (obrigatórias)

```env
JWT_SECRET=...
ADMIN_PASSWORD=...
ADMIN_REDIS_PASSWORD=...
MYSQLHOST=...
MYSQLPORT=...
MYSQLDATABASE=...
MYSQLUSER=...
MYSQLPASSWORD=...
REDIS_HOST=...
REDIS_PORT=6379
REDIS_PASSWORD=...
REDIS_SSL=true
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

## Endpoints da API (Backend)

> **Base:** definida por `VITE_API_URL` no frontend e `BACKEND_PUBLIC_URL` no backend

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

Veja o [README principal](../README.md) para mais detalhes.

## Banco de dados e auditoria

- Migrations SQL em [src/main/resources/db/migration/V1__baseline_schema.sql](src/main/resources/db/migration/V1__baseline_schema.sql)
- Auditoria habilitada com Envers em entidades de negócio críticas (`Contrato`, `Transacao`, `DesafioGamificado`)
- Índices e constraints também foram configurados no mapeamento JPA das entidades
