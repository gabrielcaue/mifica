#!/bin/bash
set -euo pipefail

REPO_URL="https://github.com/gabrielcaue/mifica.git"
BRANCH="main"
APP_DIR="/opt/mifica"
DOMAIN="api.seu-dominio.com"
EMAIL="mificaapp@gmail.com"

JWT_SECRET="troque-por-um-segredo-forte"
ADMIN_PASSWORD="troque-por-uma-senha-admin"
ADMIN_REDIS_PASSWORD="troque-por-uma-senha-admin-redis"
CORS_ALLOWED_ORIGIN_PATTERNS="https://gabrielcaue.github.io"
BACKEND_PUBLIC_URL="https://api.seu-dominio.com"

MYSQLUSER="mifica"
MYSQLPASSWORD="troque-por-uma-senha-mysql"
MYSQL_ROOT_PASSWORD="troque-por-uma-senha-root"
MYSQLDATABASE="mifica"
MYSQLHOST="mysql"
MYSQLPORT="3306"

REDIS_HOST="redis"
REDIS_PORT="6379"
REDIS_USERNAME="default"
REDIS_PASSWORD="troque-por-uma-senha-redis"
REDIS_SSL="false"

export DEBIAN_FRONTEND=noninteractive
apt-get update -y
apt-get install -y ca-certificates curl gnupg lsb-release git certbot

if ! command -v docker >/dev/null 2>&1; then
  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
  chmod a+r /etc/apt/keyrings/docker.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" > /etc/apt/sources.list.d/docker.list
  apt-get update -y
  apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
fi

systemctl enable docker
systemctl start docker

mkdir -p "$APP_DIR"
if [ ! -d "$APP_DIR/.git" ]; then
  rm -rf "$APP_DIR"
  git clone -b "$BRANCH" "$REPO_URL" "$APP_DIR"
fi

cd "$APP_DIR"
cat > .env <<EOF
JWT_SECRET=$JWT_SECRET
ADMIN_PASSWORD=$ADMIN_PASSWORD
ADMIN_REDIS_PASSWORD=$ADMIN_REDIS_PASSWORD
CORS_ALLOWED_ORIGIN_PATTERNS=$CORS_ALLOWED_ORIGIN_PATTERNS
BACKEND_PUBLIC_URL=$BACKEND_PUBLIC_URL

MYSQLHOST=$MYSQLHOST
MYSQLPORT=$MYSQLPORT
MYSQLDATABASE=$MYSQLDATABASE
MYSQLUSER=$MYSQLUSER
MYSQLPASSWORD=$MYSQLPASSWORD
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD

REDIS_HOST=$REDIS_HOST
REDIS_PORT=$REDIS_PORT
REDIS_USERNAME=$REDIS_USERNAME
REDIS_PASSWORD=$REDIS_PASSWORD
REDIS_SSL=$REDIS_SSL
EOF

mkdir -p certbot/www
sed -i "s/api\.seu-dominio\.com/$DOMAIN/g" nginx.prod.conf

docker compose -f ec2/docker-compose.ec2.yml up -d --build

certbot certonly --standalone -d "$DOMAIN" -m "$EMAIL" --agree-tos --no-eff-email || true
docker compose -f ec2/docker-compose.ec2.yml restart nginx

echo "Mifica pronto em https://$DOMAIN"
