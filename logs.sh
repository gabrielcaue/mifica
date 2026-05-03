#!/bin/zsh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo "${BLUE}📋 Mifica Logs Viewer${NC}"
echo ""

# Parse arguments
SERVICE=${1:-all}
LINES=${2:-50}
FOLLOW=${3:---tail}

# Available services
SERVICES=("backend" "frontend" "mysql" "redis" "streamlit" "traefik" "all")

# Show usage
if [ "$SERVICE" = "help" ] || [ "$SERVICE" = "-h" ] || [ "$SERVICE" = "--help" ]; then
  echo "${CYAN}Usage:${NC} ./logs.sh [SERVICE] [LINES] [--tail|--follow]"
  echo ""
  echo "${CYAN}Services:${NC}"
  echo "  backend    - Backend API (Java/Spring Boot)"
  echo "  frontend   - Frontend (React/Vite)"
  echo "  mysql      - MySQL Database"
  echo "  redis      - Redis Cache"
  echo "  streamlit  - Streamlit Analytics Dashboard"
  echo "  traefik    - Traefik Reverse Proxy"
  echo "  all        - All services (default)"
  echo ""
  echo "${CYAN}Options:${NC}"
  echo "  LINES      - Number of lines to show (default: 50)"
  echo "  --tail     - Show and stop (default)"
  echo "  --follow   - Follow logs in real-time (Ctrl+C to stop)"
  echo ""
  echo "${CYAN}Examples:${NC}"
  echo "  ./logs.sh backend 100           # Last 100 lines of backend"
  echo "  ./logs.sh backend 50 --follow   # Follow backend logs in real-time"
  echo "  ./logs.sh all                   # All services, last 50 lines"
  exit 0
fi

# Validate service
if ! echo "${SERVICES[@]}" | grep -w "$SERVICE" > /dev/null; then
  echo "${RED}❌ Unknown service: $SERVICE${NC}"
  echo "Use './logs.sh help' for available services"
  exit 1
fi

# Function to get logs from a service
get_logs() {
  local service=$1
  local lines=$2
  local follow=$3

  echo ""
  echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo "${YELLOW}📍 $service${NC}"
  echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

  case $service in
    backend)
      if docker ps | grep -q "mifica-backend"; then
        if [ "$follow" = "--follow" ]; then
          docker logs -f --tail "$lines" mifica-backend
        else
          docker logs --tail "$lines" mifica-backend
        fi
      else
        echo "${RED}❌ Container mifica-backend not running${NC}"
        return 1
      fi
      ;;

    frontend)
      if [ -f "mifica-frontend/.vite-dev-server.log" ]; then
        if [ "$follow" = "--follow" ]; then
          tail -f -n "$lines" mifica-frontend/.vite-dev-server.log 2>/dev/null || echo "${YELLOW}⚠️  Frontend not started via npm run dev${NC}"
        else
          tail -n "$lines" mifica-frontend/.vite-dev-server.log 2>/dev/null || echo "${YELLOW}⚠️  Frontend not started via npm run dev${NC}"
        fi
      else
        echo "${YELLOW}⚠️  Frontend running in local dev mode. Check terminal where 'npm run dev' is running${NC}"
      fi
      ;;

    mysql)
      if docker ps | grep -q "mysql"; then
        if [ "$follow" = "--follow" ]; then
          docker logs -f --tail "$lines" mysql
        else
          docker logs --tail "$lines" mysql
        fi
      else
        echo "${RED}❌ Container mysql not running${NC}"
        return 1
      fi
      ;;

    redis)
      if docker ps | grep -q "redis"; then
        if [ "$follow" = "--follow" ]; then
          docker logs -f --tail "$lines" redis
        else
          docker logs --tail "$lines" redis
        fi
      else
        echo "${RED}❌ Container redis not running${NC}"
        return 1
      fi
      ;;

    streamlit)
      if docker ps | grep -q "mifica-streamlit"; then
        if [ "$follow" = "--follow" ]; then
          docker logs -f --tail "$lines" mifica-streamlit
        else
          docker logs --tail "$lines" mifica-streamlit
        fi
      else
        echo "${YELLOW}⚠️  Streamlit container not running${NC}"
        return 1
      fi
      ;;

    traefik)
      if docker ps | grep -q "traefik"; then
        if [ "$follow" = "--follow" ]; then
          docker logs -f --tail "$lines" traefik
        else
          docker logs --tail "$lines" traefik
        fi
      else
        echo "${YELLOW}⚠️  Traefik not running${NC}"
        return 1
      fi
      ;;
  esac

  return 0
}

# Get logs based on service
if [ "$SERVICE" = "all" ]; then
  for svc in backend frontend mysql redis streamlit traefik; do
    get_logs "$svc" "$LINES" "$FOLLOW" || true
  done
  echo ""
  echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo "${GREEN}✅ Logs complete${NC}"
else
  get_logs "$SERVICE" "$LINES" "$FOLLOW"
fi
