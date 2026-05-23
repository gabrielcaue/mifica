#!/bin/zsh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "${BLUE}🚀 Mifica Production Deploy${NC}"
echo ""

# Check if there are uncommitted changes
if git diff-index --quiet HEAD --; then
  echo "${YELLOW}⚠️  No changes to commit.${NC}"
  exit 0
fi

# Get the list of changed files
CHANGED_FILES=$(git diff --name-only HEAD)

echo "${BLUE}📝 Changed files:${NC}"
echo "$CHANGED_FILES"
echo ""

# Analyze changed files and generate intelligent commit message
generate_commit_message() {
  local files="$1"
  local has_backend=false
  local has_frontend=false
  local has_docs=false
  local has_infra=false
  local has_blockchain=false
  local has_streamlit=false
  local has_tests=false
  local has_config=false
  
  # Count file changes per category
  local backend_count=0
  local frontend_count=0
  local test_count=0
  local config_count=0

  # Detailed categorization
  if echo "$files" | grep -qE "mifica-backend/"; then
    has_backend=true
    backend_count=$(echo "$files" | grep -c "mifica-backend/" || true)
  fi

  if echo "$files" | grep -qE "mifica-frontend/"; then
    has_frontend=true
    frontend_count=$(echo "$files" | grep -c "mifica-frontend/" || true)
  fi

  if echo "$files" | grep -qE "\.md$|docs/"; then
    has_docs=true
  fi

  if echo "$files" | grep -qE "docker-compose|\.yml|Dockerfile|nginx|Procfile|k8s/"; then
    has_infra=true
  fi

  if echo "$files" | grep -qE "blockchain|Web3|contract"; then
    has_blockchain=true
  fi

  if echo "$files" | grep -qE "mifica-streamlit/"; then
    has_streamlit=true
  fi

  if echo "$files" | grep -qE "__test__|\.test\.|test/|spec/"; then
    has_tests=true
    test_count=$(echo "$files" | grep -c "__test__\|\.test\.\|test/\|spec/" || true)
  fi

  if echo "$files" | grep -qE "pom.xml|package.json|\.yml|\.yaml|application-"; then
    has_config=true
  fi

  # Determine type and scope based on primary changes
  local type=""
  local scope=""
  local description=""

  if [ "$has_tests" = true ] && [ "$backend_count" -gt 0 ]; then
    type="test"
    scope="backend"
    description="add/update integration tests"
  elif [ "$has_blockchain" = true ]; then
    type="feat"
    scope="blockchain"
    description="enhance blockchain integration and smart contracts"
  elif [ "$has_backend" = true ] && [ "$has_frontend" = true ]; then
    type="feat"
    scope="full-stack"
    description="synchronize backend and frontend updates"
  elif [ "$has_backend" = true ]; then
    type="feat"
    scope="backend"
    description="improve backend services and APIs"
  elif [ "$has_frontend" = true ]; then
    type="feat"
    scope="frontend"
    description="enhance UI/UX and frontend components"
  elif [ "$has_streamlit" = true ]; then
    type="feat"
    scope="analytics"
    description="update analytics dashboard and reporting"
  elif [ "$has_infra" = true ]; then
    type="ci"
    scope="infrastructure"
    description="optimize deployment and container configuration"
  elif [ "$has_config" = true ]; then
    type="chore"
    scope="config"
    description="update configuration and build settings"
  elif [ "$has_docs" = true ]; then
    type="docs"
    scope="documentation"
    description="enhance technical documentation and guides"
  else
    type="chore"
    scope="project"
    description="update project files and dependencies"
  fi

  # Format: type(scope): description
  echo "$type($scope): $description"
}

# Generate intelligent commit message
COMMIT_MSG=$(generate_commit_message "$CHANGED_FILES")

# Display summary with file counts
echo "${BLUE}📊 Change Summary:${NC}"
TOTAL_FILES=$(echo "$CHANGED_FILES" | wc -l)
BACKEND_FILES=$(echo "$CHANGED_FILES" | grep -c "mifica-backend/" || echo 0)
FRONTEND_FILES=$(echo "$CHANGED_FILES" | grep -c "mifica-frontend/" || echo 0)
TESTS_FILES=$(echo "$CHANGED_FILES" | grep -cE "test|spec" || echo 0)

echo "   Total de arquivos alterados: $TOTAL_FILES"
[ "$BACKEND_FILES" -gt 0 ] && echo "   Backend: $BACKEND_FILES arquivos"
[ "$FRONTEND_FILES" -gt 0 ] && echo "   Frontend: $FRONTEND_FILES arquivos"
[ "$TESTS_FILES" -gt 0 ] && echo "   Testes: $TESTS_FILES arquivos"
echo ""

echo "${BLUE}💬 Generated commit message:${NC}"
echo "${GREEN}$COMMIT_MSG${NC}"
echo ""

# Stage all changes
echo "${BLUE}📦 Staging changes...${NC}"
git add .

# Commit with generated message
echo "${BLUE}📝 Committing to master...${NC}"
git commit -m "$COMMIT_MSG"

# Push to master
echo "${BLUE}⬆️  Pushing to GitHub (master branch)...${NC}"
git push origin master

echo ""
echo "${GREEN}✅ Deploy iniciado com sucesso!${NC}"
echo "${BLUE}🔄 GitHub Actions será executado:${NC}"
echo "   1. Build Backend (Maven + Java 17 LTS)"
echo "   2. Testes Integrados & Análise SonarQube"
echo "   3. Build Frontend (Node.js)"
echo "   4. Build Streamlit Dashboard"
echo "   5. Deploy Automático no Railway"
echo ""
echo "${YELLOW}🔗 Monitore em:${NC} https://github.com/gabrielcaue/mifica/actions"
echo ""
echo "${BLUE}📨 Commit Message:${NC}"
echo "   ${GREEN}$COMMIT_MSG${NC}"
