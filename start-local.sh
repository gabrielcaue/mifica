#!/bin/zsh

set -e

WITH_SONAR=false

if [ "${1:-}" = "--with-sonar" ]; then
  WITH_SONAR=true
fi

echo "🚀 Subindo containers do Redis, banco, backend, Traefik e Streamlit..."
docker compose up -d redis mysql mifica-backend mifica-streamlit traefik

if [ "$WITH_SONAR" = true ]; then
  echo "📊 Subindo SonarQube local..."
  docker compose --profile quality up -d sonarqube-db sonarqube
fi

echo "🌱 Iniciando frontend localmente com Vite..."
cd mifica-frontend || { echo "❌ Pasta mifica-frontend não encontrada"; exit 1; }

if [ ! -d "node_modules" ]; then
  echo "📦 Instalando dependências do frontend..."
  npm install
fi

npm run dev