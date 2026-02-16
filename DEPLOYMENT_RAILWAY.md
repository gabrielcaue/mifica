# Guia de Deployment - Railway

## 🚀 Setup Rápido no Railway

### 1. Criar Conta
- Acesse: https://railway.app
- Sign in com GitHub (mais rápido)

### 2. Criar Novo Projeto
- Click em "New Project" → "Deploy from GitHub repo"
- Selecione seu repositório `gabrielcaue/mifica`

### 3. Adicionar Serviços

#### Backend (Spring Boot)
1. Click em "New Service" → "GitHub Repo"
2. Selecione `mifica-backend` como raiz do Dockerfile
3. Configure:
   - **Dockerfile**: `Dockerfile.prod`
   - **Port**: `8080`
   - **Ambiente**: Production

#### PostgreSQL (Banco de Dados)
1. Click em "New Service" → "Database" → "PostgreSQL"
2. Railway cria automaticamente com variáveis:
   - `DATABASE_URL`
   - `DATABASE_USER`
   - `DATABASE_PASSWORD`

#### Kafka (Opcional, para testes)
Se quiser usar Kafka em produção:
- Considere usar **Upstash Kafka** (https://upstash.com) - gratuito para testes
- Ou adicionar como serviço Railway adicional

### 4. Variáveis de Ambiente

No Railway dashboard, adicione:

```env
# JWT
JWT_SECRET=sua-chave-secreta-super-segura-aqui

# Admin
ADMIN_PASSWORD=SenhaAdmin2025!

# Kafka (se usar Upstash ou outro)
KAFKA_BOOTSTRAP_SERVERS=kafka.upstash.io:9092
KAFKA_PASSWORD=seu-token-kafka

# CORS (para GitHub Pages)
CORS_ALLOWED_ORIGINS=https://gabrielcaue.github.io
```

### 5. Variáveis do Banco (Auto)
Railway cria automaticamente:
- `DATABASE_URL` → `postgresql://user:pass@host:5432/db`
- `DATABASE_USER`
- `DATABASE_PASSWORD`

### 6. Deploy Automático
Seu workflow GitHub Actions pushará automaticamente para Railway:
- Cada push em `master` dispara build + deploy
- Logs em tempo real no dashboard Railway

### 7. Obter URL da API
Após deploy bem-sucedido:
1. No Railway dashboard, clique no serviço backend
2. Copie a URL pública (ex: `https://mifica-backend-prod.railway.app`)
3. Atualize em `mifica-frontend/.env.production`:

```env
VITE_API_URL=https://mifica-backend-prod.railway.app
```

## 📊 O que Impressiona Recrutadores

✅ **Backend em produção 24/7**  
✅ **Database PostgreSQL profissional**  
✅ **CI/CD automático com GitHub Actions**  
✅ **Logs e monitoramento no Railway**  
✅ **Variáveis de ambiente seguras**  
✅ **Multi-stage Docker otimizado**  
✅ **SSL/HTTPS automático**  

## 🔗 Links Úteis

- Railway Dashboard: https://railway.app/dashboard
- Railway Docs: https://docs.railway.app
- Seu Backend (após deploy): `https://seu-projeto.railway.app`

## 💡 Dicas

1. **Primeiro Deploy**: Railway detecta automaticamente Spring Boot
2. **Logs**: Veja em tempo real no dashboard
3. **Rollback**: Histórico de deploys com um clique
4. **Free Tier**: $5/mês de crédito grátis (suficiente)
5. **Scaling**: Se crescer, upgrade é super simples

---

**Próximos passos**:
1. Criar conta Railway
2. Adicionar `RAILWAY_TOKEN` em GitHub Secrets
3. Fazer push para disparar CI/CD
4. Verificar logs
5. Copiar URL da API e atualizar frontend
