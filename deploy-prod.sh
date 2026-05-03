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

# Analyze changed files and generate commit message
generate_commit_message() {
  local files="$1"
  local has_backend=false
  local has_frontend=false
  local has_docs=false
  local has_infra=false
  local has_blockchain=false
  local has_streamlit=false

  # Categorize changes
  if echo "$files" | grep -qE "mifica-backend/|pom.xml"; then
    has_backend=true
  fi

  if echo "$files" | grep -qE "mifica-frontend/|package.json"; then
    has_frontend=true
  fi

  if echo "$files" | grep -qE "\.md$|docs/"; then
    has_docs=true
  fi

  if echo "$files" | grep -qE "docker-compose|\.yml|Dockerfile|nginx|Procfile"; then
    has_infra=true
  fi

  if echo "$files" | grep -qE "mifica-backend.*blockchain|Web3"; then
    has_blockchain=true
  fi

  if echo "$files" | grep -qE "mifica-streamlit/"; then
    has_streamlit=true
  fi

  # Generate message based on changes
  local scope=""
  local description=""

  if [ "$has_blockchain" = true ]; then
    scope="blockchain"
    description="update blockchain integrations"
  elif [ "$has_backend" = true ] && [ "$has_frontend" = true ]; then
    scope="full-stack"
    description="update backend and frontend services"
  elif [ "$has_backend" = true ]; then
    scope="backend"
    description="update backend service"
  elif [ "$has_frontend" = true ]; then
    scope="frontend"
    description="update frontend application"
  elif [ "$has_streamlit" = true ]; then
    scope="streamlit"
    description="update analytics dashboard"
  elif [ "$has_infra" = true ]; then
    scope="infra"
    description="update infrastructure and deployment config"
  elif [ "$has_docs" = true ]; then
    scope="docs"
    description="update documentation"
  else
    scope="chore"
    description="update project files"
  fi

  # Format: type(scope): description
  echo "chore($scope): $description"
}

COMMIT_MSG=$(generate_commit_message "$CHANGED_FILES")

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
echo "${GREEN}✅ Deploy initiated!${NC}"
echo "${BLUE}📊 GitHub Actions will now:${NC}"
echo "   1. Build backend with Maven (Java 21)"
echo "   2. Run tests and SonarQube analysis"
echo "   3. Trigger Railway auto-deployment"
echo ""
echo "${YELLOW}Monitor at:${NC} https://github.com/gabrielcaue/mifica/actions"
