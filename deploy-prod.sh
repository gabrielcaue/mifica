#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="docker-compose.prod.yml"

if [[ ! -f .env ]]; then
  echo "[ERRO] .env não encontrado. Copie .env.example para .env"
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
    docker compose -f "$COMPOSE_FILE" down || true

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
    docker compose -f "$COMPOSE_FILE" up -d --build

    echo "[OK] HTTPS configurado. Teste: https://$domain/health"
    ;;
  up)
    docker compose -f "$COMPOSE_FILE" up -d --build
    ;;
  down)
    docker compose -f "$COMPOSE_FILE" down
    ;;
  restart)
    docker compose -f "$COMPOSE_FILE" down
    docker compose -f "$COMPOSE_FILE" up -d --build
    ;;
  logs)
    docker compose -f "$COMPOSE_FILE" logs -f --tail=200
    ;;
  status)
    docker compose -f "$COMPOSE_FILE" ps
    ;;
  pull)
    git pull --rebase
    docker compose -f "$COMPOSE_FILE" up -d --build
    ;;
  *)
    echo "Uso: ./deploy-prod.sh {init-ssl <dominio> <email>|up|down|restart|logs|status|pull}"
    exit 1
    ;;
esac
