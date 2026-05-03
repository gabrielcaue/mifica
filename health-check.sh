#!/bin/zsh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "${BLUE}🏥 Mifica Health Check${NC}"
echo ""

# Determine environment
ENV=${1:-local}
case $ENV in
  local|dev)
    BACKEND_URL="http://localhost:8080"
    REDIS_HOST="localhost"
    REDIS_PORT="6379"
    MYSQL_HOST="localhost"
    MYSQL_PORT="3306"
    ;;
  prod|production)
    # In production, these should be set via environment variables
    BACKEND_URL=${BACKEND_URL:-"https://mifica-api.railway.app"}
    REDIS_HOST=${REDIS_HOST:-""}
    REDIS_PORT=${REDIS_PORT:-""}
    MYSQL_HOST=${MYSQL_HOST:-""}
    MYSQL_PORT=${MYSQL_PORT:-""}
    ;;
  *)
    echo "${RED}❌ Unknown environment: $ENV${NC}"
    echo "Usage: ./health-check.sh [local|prod]"
    exit 1
    ;;
esac

echo "${BLUE}Environment: $ENV${NC}"
echo ""

# Counter for health status
HEALTHY=0
TOTAL=0

# Function to check a service
check_service() {
  local name=$1
  local check_fn=$2

  TOTAL=$((TOTAL + 1))
  echo -n "${YELLOW}⏳ Checking $name...${NC} "

  if $check_fn; then
    echo "${GREEN}✅${NC}"
    HEALTHY=$((HEALTHY + 1))
  else
    echo "${RED}❌${NC}"
  fi
}

# Backend health check
check_backend() {
  if command -v curl &> /dev/null; then
    curl -sf "$BACKEND_URL/actuator/health" > /dev/null 2>&1
  else
    nc -z -w 2 localhost 8080 > /dev/null 2>&1
  fi
}

# Redis health check
check_redis() {
  if [ -z "$REDIS_HOST" ]; then
    echo "${YELLOW}(prod config not set)${NC}"
    return 1
  fi
  
  if command -v redis-cli &> /dev/null; then
    redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping > /dev/null 2>&1
  else
    nc -z -w 2 "$REDIS_HOST" "$REDIS_PORT" > /dev/null 2>&1
  fi
}

# MySQL health check
check_mysql() {
  if [ -z "$MYSQL_HOST" ]; then
    echo "${YELLOW}(prod config not set)${NC}"
    return 1
  fi

  if command -v mysql &> /dev/null; then
    mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u root -e "SELECT 1" > /dev/null 2>&1
  else
    nc -z -w 2 "$MYSQL_HOST" "$MYSQL_PORT" > /dev/null 2>&1
  fi
}

# Docker containers check (local only)
check_docker_containers() {
  if [ "$ENV" != "local" ] && [ "$ENV" != "dev" ]; then
    return 0
  fi

  local containers=("mifica-backend" "mysql" "redis" "traefik")
  local all_running=true

  for container in "${containers[@]}"; do
    if ! docker ps --format '{{.Names}}' | grep -q "^${container}$"; then
      all_running=false
      break
    fi
  done

  [ "$all_running" = true ]
}

# Database migration check (local only)
check_database_migrations() {
  if [ "$ENV" != "local" ] && [ "$ENV" != "dev" ]; then
    return 0
  fi

  if command -v curl &> /dev/null; then
    # Check if backend has connected to database
    curl -sf "$BACKEND_URL/actuator/health/db" > /dev/null 2>&1
  else
    return 0
  fi
}

# Run checks
echo "${BLUE}Running health checks...${NC}"
echo ""

check_service "Backend ($BACKEND_URL)" check_backend
check_service "Redis ($REDIS_HOST:$REDIS_PORT)" check_redis
check_service "MySQL ($MYSQL_HOST:$MYSQL_PORT)" check_mysql
check_service "Docker Containers" check_docker_containers
check_service "Database Migrations" check_database_migrations

# Summary
echo ""
echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ $HEALTHY -eq $TOTAL ]; then
  echo "${GREEN}✅ All systems healthy ($HEALTHY/$TOTAL)${NC}"
  exit 0
else
  UNHEALTHY=$((TOTAL - HEALTHY))
  echo "${RED}❌ Some systems unhealthy ($HEALTHY/$TOTAL healthy, $UNHEALTHY failed)${NC}"
  exit 1
fi
