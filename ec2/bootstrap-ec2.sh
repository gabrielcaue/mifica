#!/usr/bin/env bash
set -euo pipefail

REPO_URL="${1:-}"
APP_DIR="/opt/mifica"
BRANCH="${BRANCH:-main}"
DOMAIN="${DOMAIN:-api.seu-dominio.com}"
EMAIL="${EMAIL:-}"

if [[ -z "$REPO_URL" ]]; then
  echo "Uso: ./bootstrap-ec2.sh <repo-url>"
  exit 1
fi

if [[ -z "$EMAIL" ]]; then
  echo "Defina EMAIL no ambiente antes de rodar para o Let's Encrypt."
  exit 1
fi

sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg lsb-release git

if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list >/dev/null
  sudo apt-get update -y
  sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
fi

sudo systemctl enable docker
sudo systemctl start docker

sudo mkdir -p "$APP_DIR"
if [[ ! -d "$APP_DIR/.git" ]]; then
  sudo rm -rf "$APP_DIR"
  sudo git clone -b "$BRANCH" "$REPO_URL" "$APP_DIR"
fi

cd "$APP_DIR"

if [[ ! -f .env ]]; then
  echo "[ERRO] .env não encontrado em $APP_DIR. Copie ec2/.env.example para .env e ajuste os valores."
  exit 1
fi

sudo mkdir -p certbot/www

sed -i.bak "s/api\.seu-dominio\.com/$DOMAIN/g" nginx.prod.conf || true

sudo docker compose -f ec2/docker-compose.ec2.yml up -d --build

if ! command -v certbot >/dev/null 2>&1; then
  sudo apt-get install -y certbot
fi

sudo certbot certonly --standalone -d "$DOMAIN" -m "$EMAIL" --agree-tos --no-eff-email || true
sudo docker compose -f ec2/docker-compose.ec2.yml restart nginx

echo "EC2 pronto. Acesse: https://$DOMAIN"
