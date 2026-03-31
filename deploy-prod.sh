#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="docker-compose.prod.yml"

if [[ ! -f .env ]]; then
  echo "[ERRO] .env não encontrado. Copie .env.example para .env"
  exit 1
fi

cmd="${1:-status}"

case "$cmd" in
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
    echo "Uso: ./deploy-prod.sh {up|down|restart|logs|status|pull}"
    exit 1
    ;;
esac
