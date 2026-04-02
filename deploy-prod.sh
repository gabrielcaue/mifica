#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="ec2/docker-compose.ec2.yml"
ENV_FILE=".env"

compose() {
  docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" "$@"
}

if [[ ! -f .env ]]; then
  echo "[ERRO] .env não encontrado. Copie ec2/.env.example para .env"
  exit 1
fi

cmd="${1:-status}"

case "$cmd" in
  init-ssl)
    domain="${2:-}"
    email="${3:-}"

    if [[ -z "$domain" || -z "$email" ]]; then
      echo "Uso: ./deploy-prod.sh init-ssl <dominio> <email>"
      exit 1
    fi

    if [[ "$(uname -s)" != "Linux" ]]; then
      echo "[ERRO] init-ssl deve ser executado no servidor Linux (EC2)."
      exit 1
    fi

    mkdir -p certbot/www

    echo "[INFO] Parando stack para liberar porta 80..."
    compose down || true

    if ! command -v certbot >/dev/null 2>&1; then
      echo "[INFO] Instalando certbot..."
      sudo apt-get update -y
      sudo apt-get install -y certbot
    fi

    echo "[INFO] Gerando certificado Let's Encrypt para $domain..."
    sudo certbot certonly --standalone -d "$domain" -m "$email" --agree-tos --no-eff-email

    echo "[INFO] Ajustando nginx.prod.conf com domínio real..."
    sed -i.bak "s/api\.seu-dominio\.com/$domain/g" nginx.prod.conf

    echo "[INFO] Subindo stack com HTTPS..."
    compose up -d --build

    echo "[OK] HTTPS configurado. Teste: https://$domain/health"
    ;;
  up)
    compose up -d --build
    ;;
  down)
    compose down
    ;;
  restart)
    compose down
    compose up -d --build
    ;;
  logs)
    compose logs -f --tail=200
    ;;
  status)
    compose ps
    ;;
  pull)
    git pull --rebase
    compose up -d --build
    ;;
  *)
    echo "Uso: ./deploy-prod.sh {init-ssl <dominio> <email>|up|down|restart|logs|status|pull}"
    exit 1
    ;;
esac
