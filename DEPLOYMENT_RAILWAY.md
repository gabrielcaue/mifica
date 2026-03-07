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

#### MySQL (Banco de Dados)
1. Click em "New Service" → "Database" → "MySQL"
2. Railway cria automaticamente variáveis como:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

#### Kafka (opcional, recomendado para gamificação/eventos)
Use um provedor de Kafka gerenciado (ex.: Upstash) e configure no backend:
- `KAFKA_BOOTSTRAP_SERVERS`
- `KAFKA_SECURITY_PROTOCOL`
- `KAFKA_SASL_MECHANISM`
- `KAFKA_USERNAME`
- `KAFKA_PASSWORD`
- `ADMIN_KAFKA_PASSWORD`

### 4. Configurar Variáveis de Ambiente no Railway

#### 📋 Passo a passo:

1. **Acesse seu projeto no Railway**
   - Entre em https://railway.app/dashboard
   - Clique no seu projeto (mifica ou o nome que você deu)

2. **Selecione o serviço backend**
   - No dashboard do projeto, clique no card do **mifica-backend**

3. **Abra a aba Variables**
   - No menu superior, clique em **"Variables"** (ícone de chave 🔑)

4. **Adicione as variáveis obrigatórias uma por uma:**

   Clique em **"+ New Variable"** e adicione:

   **JWT / Admin:**
   ```
   JWT_SECRET = (gerar - veja abaixo como criar)
   ADMIN_PASSWORD = SenhaAdmin2026!
   ADMIN_KAFKA_PASSWORD = senha-especial-admin-kafka
   ```

   **🔐 Como gerar o JWT_SECRET:**
   
   Você precisa criar uma chave secreta forte e aleatória. Escolha uma opção:
   
   **Opção 1 - No terminal (Mac/Linux):**
   ```bash
   openssl rand -base64 64
   ```
   
   **Opção 2 - No navegador (qualquer sistema):**
   - Acesse: https://randomkeygen.com/
   - Copie uma chave da seção "Fort Knox Passwords" (256-bit)
   
   **Opção 3 - Node.js:**
   ```bash
   node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
   ```
   
   **Opção 4 - Python:**
   ```bash
   python3 -c "import secrets; print(secrets.token_urlsafe(64))"
   ```
   
   > ⚠️ **Importante:** Guarde essa chave em local seguro! Ela é usada para assinar os tokens JWT. Se perder, todos os usuários terão que fazer login novamente.

   **MySQL (copie do serviço MySQL):**
   
   As variáveis de MySQL já existem no serviço de banco. Você pode:
   - Opção 1: Deixar o Railway linkar automaticamente (Reference Variables)
   - Opção 2: Copiar manualmente do serviço MySQL:
   ```
   MYSQLHOST = (copiar do serviço MySQL)
   MYSQLPORT = (copiar do serviço MySQL)
   MYSQLDATABASE = (copiar do serviço MySQL)
   MYSQLUSER = (copiar do serviço MySQL)
   MYSQLPASSWORD = (copiar do serviço MySQL)
   ```

   **Kafka (se usar Upstash ou outro provedor):**
   ```
   KAFKA_BOOTSTRAP_SERVERS = seu-kafka-bootstrap-servers
   KAFKA_SECURITY_PROTOCOL = SASL_SSL
   KAFKA_SASL_MECHANISM = PLAIN
   KAFKA_USERNAME = seu-kafka-username
   KAFKA_PASSWORD = seu-kafka-password
   ```

   **CORS:**
   ```
   CORS_ALLOWED_ORIGIN_PATTERNS = *
   ```

5. **Salvar e aguardar redeploy**
   - Railway redeploya automaticamente após adicionar variáveis
   - Acompanhe os logs na aba **"Deployments"**


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
✅ **Database MySQL profissional**  
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
