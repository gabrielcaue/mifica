#!/bin/zsh

echo "🚀 Subindo containers do backend, banco e streamlit..."
docker compose up -d mysql mifica-backend mifica-streamlit

echo "🌱 Iniciando frontend localmente com Vite..."
cd mifica-frontend || { echo "❌ Pasta mifica-frontend não encontrada"; exit 1; }

# Instala dependências (só na primeira vez ou quando mudar package.json)
if [ ! -d "node_modules" ]; then
  echo "📦 Instalando dependências do frontend..."
  npm install
fi

npm run dev
