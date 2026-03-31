# Deploy AWS Free Tier com Nginx (Mifica)

## Objetivo (SDD)
- Expor publicamente apenas `80/443`.
- Colocar `Nginx` como edge reverse proxy.
- Manter `backend`, `streamlit` e demais serviços sem exposição pública direta.

## Decisões (ICP-CDD)
- `Nginx` centraliza roteamento HTTP (`/`, `/api`, `/streamlit`).
- `ALB` (opcional, recomendado) termina TLS em `443` e encaminha para EC2 em `80`.
- `docker-compose.prod.yml` separado do ambiente dev para reduzir risco operacional.

## Arquivos principais
- [docker-compose.prod.yml](docker-compose.prod.yml)
- [nginx.prod.conf](nginx.prod.conf)
- [.env.example](.env.example)
- [deploy-prod.sh](deploy-prod.sh)

## 1) Segurança de rede (AWS)
Security Group da EC2:
- `22` -> somente seu IP
- `80` -> público (ou somente SG do ALB)
- `443` -> público (se TLS direto na EC2)

Não abrir:
- `8080`, `8501`, `3306`, `6379`

## 2) Preparação da EC2
```bash
sudo yum update -y
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user
```

Instalar plugin compose (se necessário):
```bash
DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
mkdir -p $DOCKER_CONFIG/cli-plugins
curl -SL https://github.com/docker/compose/releases/download/v2.29.7/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose
```

## 3) Deploy
```bash
cp .env.example .env
# editar .env com valores reais
chmod +x deploy-prod.sh
./deploy-prod.sh up
./deploy-prod.sh status
```

Teste:
```bash
curl http://SEU_HOST/health
curl http://SEU_HOST/api/actuator/health
```

## 4) TLS profissional (recomendado no Free Trial)
### Opção A (mais simples): ALB + ACM
- ACM gerencia certificado.
- ALB recebe `443` e encaminha para EC2 `80`.
- EC2 continua com Nginx como reverse proxy.

### Opção B: TLS direto no Nginx
- Usar certbot na EC2.
- Exige manutenção e renovação local de certificado.

## 5) Critérios de aceite (SDD)
- `80/443` são as únicas portas públicas.
- `/health` retorna 200 via Nginx.
- `/api/*` responde pelo backend via proxy.
- `/streamlit/*` responde via Nginx.
- Serviços internos não acessíveis diretamente da internet.
