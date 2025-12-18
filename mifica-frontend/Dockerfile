# Etapa 1: build da aplicação
FROM node:20-alpine AS build
WORKDIR /app

# Copia apenas os arquivos de dependência primeiro para aproveitar cache
COPY package*.json ./
RUN npm install --frozen-lockfile

# Copia o restante do código e roda o build
COPY . .
RUN npm run build

# Etapa 2: servidor web para servir os arquivos
FROM nginx:alpine

# Remove conteúdo padrão do nginx
RUN rm -rf /usr/share/nginx/html/*

# Copia os arquivos gerados pelo build
COPY --from=build /app/dist /usr/share/nginx/html

# Copia configuração customizada para suportar SPA (React Router)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expõe a porta 80 (Nginx padrão)
EXPOSE 80

# Nginx já inicia automaticamente
