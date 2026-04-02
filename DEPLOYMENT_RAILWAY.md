# Guia de Deployment - Railway

## Setup rápido

1. Acesse https://railway.app e faça login com GitHub.
2. Crie um projeto a partir do repositório `gabrielcaue/mifica`.
3. Adicione os serviços necessários:
   - Backend: raiz `mifica-backend`, Dockerfile `Dockerfile.prod`, porta `8080`.
   - MySQL: use o banco gerenciado do Railway.
   - Redis: use Upstash e conecte ao backend.

## Variáveis obrigatórias

Configure estas variáveis no serviço do backend:

- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET`
- `ADMIN_PASSWORD`
- `ADMIN_REDIS_PASSWORD`
- `CORS_ALLOWED_ORIGIN_PATTERNS`
- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLDATABASE`
- `MYSQLUSER`
- `MYSQLPASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_USERNAME`
- `REDIS_PASSWORD`
- `REDIS_SSL`
- `BACKEND_PUBLIC_URL`

Valores recomendados para este ambiente:

```env
SPRING_PROFILES_ACTIVE=prod
ADMIN_PASSWORD=change-me
ADMIN_REDIS_PASSWORD=change-me
JWT_SECRET=change-me-with-a-strong-random-secret
CORS_ALLOWED_ORIGIN_PATTERNS=https://gabrielcaue.github.io,http://localhost:5173
MYSQLHOST=mysql.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=change-me
REDIS_HOST=able-giraffe-65440.upstash.io
REDIS_PORT=6379
REDIS_USERNAME=default
REDIS_PASSWORD=change-me
REDIS_SSL=true
BACKEND_PUBLIC_URL=https://seu-projeto.railway.app
```

## Publicação

- O Railway redeploya automaticamente quando as variáveis são salvas.
- Depois do deploy, copie a URL pública do serviço backend e use esse valor em `BACKEND_PUBLIC_URL`.
- Atualize o frontend para apontar para a nova API publicada no Railway.

## Pós-migração

Os artefatos antigos de infraestrutura foram removidos para evitar mistura de plataformas.
