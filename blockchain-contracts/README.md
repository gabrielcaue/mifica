# Mifica Blockchain Contracts (Hardhat)

This folder contains a minimal Hardhat project to compile and deploy a simple `Badge` contract to Polygon (Mumbai for testing and Polygon mainnet for production).

Setup

1. Install Node (>=16) and npm.
2. From this folder install deps:

```bash
cd blockchain-contracts
npm install
```

3. Create `.env` from `.env.example` and fill `POLYGON_RPC_URL` and `DEPLOYER_PRIVATE_KEY`.

Deploy (Polygon mainnet)

1. Ensure your deployer address (the private key) holds enough MATIC. Transfer MATIC from Coinbase to your MetaMask/deployer address and verify Polygon network is selected during withdrawal.
2. Set env vars (example):

```bash
export POLYGON_RPC_URL="https://your-quicknode-or-alchemy-polygon-url"
export DEPLOYER_PRIVATE_KEY="0x..."
```

3. Deploy:

```bash
npx hardhat run --network polygon scripts/deploy.js
```

Notes about payment of gas
- The gas for deployment and any on-chain write is paid by the deployer account (MATIC). Alchemy/QuickNode only provide RPC access — they do not cover gas fees.
- When withdrawing MATIC from Coinbase, choose the Polygon (MATIC) network for mainnet withdrawals. Gas fees are paid on-chain by your wallet balance.
- Recommended starting buffer: 10 MATIC for deploy + initial tests on mainnet.

Testing (Mumbai)

Use `MUMBAI_RPC_URL` and a deployer key with test MATIC, and run:

```bash
npx hardhat run --network mumbai scripts/deploy.js
```

Security
- Never commit `.env` with private keys.
- Use a secrets manager for production keys.
- Test thoroughly on Mumbai before mainnet deploy.
