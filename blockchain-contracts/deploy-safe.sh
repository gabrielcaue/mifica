#!/usr/bin/env bash
# Safe deploy wrapper for Badge contract
# - validates DEPLOYER_PRIVATE_KEY and POLYGON_RPC_URL
# - checks deployer MATIC balance (MIN_MATIC, default 5)
# - supports running via Railway CLI (railway run) or locally

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"/..
cd "$ROOT_DIR/blockchain-contracts"

usage() {
  echo "Usage: $0 [--railway]"
  echo "  --railway   run deploy via 'railway run' to use Railway secrets"
  exit 1
}

USE_RAILWAY=0
if [[ "${1:-}" == "--railway" ]]; then
  USE_RAILWAY=1
fi

if [[ $USE_RAILWAY -eq 1 ]]; then
  echo "Running deploy through Railway CLI (will use Railway secrets)."
  echo "Checking balance via Railway-injected env..."
  railway run node ./scripts/checkBalance.js
  echo "Balance OK. Proceed with deploy? (y/N)"
  read -r yn
  if [[ "$yn" != "y" && "$yn" != "Y" ]]; then
    echo "Aborted."
    exit 0
  fi
  railway run npx hardhat run --network polygon scripts/deploy.js
  exit 0
fi

# Local mode: require env vars in current shell or in .env
if [[ -f .env ]]; then
  # load .env for local convenience (but encourage using exports)
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

if [[ -z "${DEPLOYER_PRIVATE_KEY:-}" ]]; then
  echo "DEPLOYER_PRIVATE_KEY not set. Export it in your shell or use --railway." >&2
  exit 2
fi

if [[ -z "${POLYGON_RPC_URL:-}" ]]; then
  echo "POLYGON_RPC_URL not set. Export it in your shell or set in .env." >&2
  exit 3
fi

echo "Checking deployer balance..."
node ./scripts/checkBalance.js

read -r -p "Balance OK. Proceed with deploy to Polygon mainnet? (type 'yes' to continue) " confirm
if [[ "$confirm" != "yes" ]]; then
  echo "Aborted by user."; exit 0
fi

echo "Running Hardhat deploy..."
npx hardhat run --network polygon scripts/deploy.js

echo "Deploy finished."
