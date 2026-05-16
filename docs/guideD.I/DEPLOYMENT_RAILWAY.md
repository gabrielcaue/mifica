# Guia de Deployment - Railway

## Setup rápido

1. Acesse https://railway.app e faça login com GitHub.
2. Crie um projeto a partir do repositório `gabrielcaue/mifica`.
3. Adicione os serviços necessários:
   - Backend: raiz `mifica-backend`, Dockerfile `Dockerfile.prod`, porta `8080`.
   - MySQL: use o banco gerenciado do Railway.
   - Redis: use Upstash e conecte ao backend.
   - Prometheus: crie um serviço separado apontando para `prometheus/Dockerfile` e exponha a porta `9090`.
   - Grafana: crie um serviço separado apontando para `grafana/Dockerfile` e exponha a porta `3000`.

## Tipo de serviço no Railway

- Para **Prometheus** e **Grafana**, use **GitHub Repo**.
- Não use `Database`, `Bucket`, `Function` nem `Empty Service`.
- Não use `Docker Image` a menos que você queira publicar imagens manualmente em um registry externo.
- No serviço GitHub Repo, aponte o Dockerfile correto:
   - Prometheus: `prometheus/Dockerfile`
   - Grafana: `grafana/Dockerfile`

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

Para o Grafana, defina também:

- `GF_SECURITY_ADMIN_USER=admin`
- `GF_SECURITY_ADMIN_PASSWORD=uma-senha-forte`
- `GF_SERVER_ROOT_URL=https://<seu-servico>.up.railway.app/`
- `GF_SERVER_SERVE_FROM_SUB_PATH=false`
- `PROMETHEUS_URL=http://<nome-do-servico-prometheus>.railway.internal:9090`

O Dockerfile do Grafana já leva os arquivos de provisionamento e dashboards do repositório para dentro da imagem.

No Prometheus, ajuste o target do backend em `monitoring/prometheus/prometheus.yml` para a URL do serviço do Railway, para permitir o scrape do endpoint `/actuator/prometheus`.
No ambiente local, o target permanece `mifica-backend:8080`.
Para o Prometheus no Railway, defina:

- `BACKEND_TARGET=<nome-do-servico-backend>.railway.internal:8080`
- `BACKEND_SCHEME=http`

Se o backend estiver fora da rede interna do mesmo projeto Railway, use:

- `BACKEND_TARGET=<dominio-publico-backend>:443`
- `BACKEND_SCHEME=https`

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
- Use a URL pública do Grafana para acompanhar métricas e dashboards.
- Mantenha o SonarQube fora do Railway; execute localmente com `./start-local.sh --with-sonar` quando quiser analisar qualidade.

## Pós-migração

Os artefatos antigos de infraestrutura foram removidos para evitar mistura de plataformas.
