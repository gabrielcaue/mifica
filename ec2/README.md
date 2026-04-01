# Deploy em EC2

Este diretório concentra o deploy final do Mifica em uma instância EC2 com Docker Compose.

## Arquivos

- `docker-compose.ec2.yml` — stack completa com Nginx, backend, frontend, Streamlit, MySQL e Redis
- `.env.example` — variáveis de ambiente para produção
- `bootstrap-ec2.sh` — bootstrap da instância
- `user-data.sh` — script para colar no campo User data do EC2

## Fluxo recomendado

1. Criar a instância EC2 com Ubuntu 22.04 ou similar
2. Abrir portas `80` e `443` no security group
3. Copiar `ec2/.env.example` para `.env` na raiz do projeto e preencher os segredos
4. Executar `deploy-prod.sh up` ou `ec2/bootstrap-ec2.sh <repo-url>`

## Bootstrap automático

Se preferir, cole o conteúdo de [user-data.sh](user-data.sh) no campo **User data** ao criar a EC2. Antes de colar, substitua os placeholders de domínio, e-mail e segredos. Nesse modo a instância instala Docker, clona o repositório, gera o `.env` e sobe a stack sozinha.

## Observação

Kubernetes continua disponível como material técnico em [k8s/](../k8s/), mas a publicação final da aplicação acontece em EC2.
