# Badge Contract Integration Guide

## Overview

This document describes the integration between the Mifica backend and the Badge smart contract deployed on Polygon mainnet.

**Contract Address:** `0xC5e...` (Replace with actual address)  
**Network:** Polygon (chainId=137)  
**RPC Endpoint:** Alchemy or QuickNode (configured via `POLYGON_RPC_URL`)

---

## Architecture

### Flow: Create Badge (MetaMask Signing)

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (React)                          │
│                                                                   │
│  1. User clicks "Create Badge"                                   │
│  2. Build tx data: ethers.ContractFactory.createBadge(...)       │
│  3. User signs with MetaMask (eth_sendTransaction)               │
│  4. MetaMask returns txHash                                       │
│  5. POST /api/blockchain/badges { txHash, toAddress, ... }       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     Backend (Spring Boot)                         │
│                                                                   │
│  POST /api/blockchain/badges                                     │
│  ├─ Validate chainId (must be 137)                              │
│  ├─ Check idempotency (txHash already in DB?)                    │
│  └─ Poll RPC for receipt (up to 60 attempts, 5s intervals)       │
│      ├─ If receipt.status == 1 (success):                        │
│      │   └─ Persist TransacaoBlockchain with txHash              │
│      │       Return 201 Created with badge details               │
│      └─ If receipt.status == 0 (failed):                         │
│          └─ Return 500 "Transaction failed on-chain"             │
│                                                                   │
│  Guardianship:                                                    │
│  ├─ ✓ Private key never exposed (Railway secrets only)           │
│  ├─ ✓ ChainId validation (prevent mainnet/testnet confusion)    │
│  ├─ ✓ Idempotency (same txHash won't re-persist)                │
│  └─ ✓ Receipts validated (only confirmed txs persisted)          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Polygon Mainnet                                │
│                                                                   │
│  Badge.sol: createBadge(address to, string metadata)             │
│  ├─ Emit BadgeCreated(msg.sender, to, id, metadata)              │
│  └─ Increment nextId++                                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## Backend Components

### 1. `BadgeContractService` (new)
**File:** `mifica-backend/src/main/java/com/mifica/blockchain/BadgeContractService.java`

Encapsulates Web3j interactions:
- `buildCreateBadgeData()` — builds encoded function call data for `createBadge`
- `callGetNextId()` — read-only call to fetch current badge counter
- `validateContractConfiguration()` — ensures contract is properly configured

### 2. `BlockchainService.registrarBadge()`
**File:** `mifica-backend/src/main/java/com/mifica/blockchain/BlockchainService.java`

Handles badge registration:
- Validates chain ID (must be 137)
- Checks for idempotency (prevents double-persisting)
- Polls RPC for transaction receipt (up to 60 attempts)
- Persists confirmed badge to `TransacaoBlockchain` table

### 3. `CreateBadgeDTO` (new)
**File:** `mifica-backend/src/main/java/com/mifica/dto/CreateBadgeDTO.java`

Request DTO:
```json
{
  "txHash": "0x1a2b3c...",
  "toAddress": "0xEfb6885db2A30D15c861d013cB74F3B3E3c221fb",
  "metadata": "{\"name\": \"Achievement Badge\", \"rarity\": \"rare\"}",
  "chainId": 137
}
```

### 4. `BlockchainController.criarBadge()`
**File:** `mifica-backend/src/main/java/com/mifica/controller/BlockchainController.java`

REST Endpoint:
```
POST /api/blockchain/badges
Content-Type: application/json

{
  "txHash": "0x...",
  "toAddress": "0x...",
  "metadata": "...",
  "chainId": 137
}

Response: 201 Created
{
  "id": 1,
  "hashTransacao": "0x...",
  "remetente": "0x...",
  "destinatario": "0x...",
  "valor": 0.0,
  "dataTransacao": "2026-09-13T15:30:00"
}
```

---

## Configuration

### Environment Variables (Railway)

Set these in your Railway project:

```bash
# RPC Provider
POLYGON_RPC_URL=https://polygon-mainnet.g.alchemy.com/v2/YOUR_KEY

# Badge Contract Address (set after deploy)
BADGE_CONTRACT_ADDRESS=0xC5e...

# Optional: Private key (for backend-initiated deployments)
DEPLOYER_PRIVATE_KEY=0x...
```

### Spring Properties (`application.properties`)

```properties
blockchain.polygon.chain-id=137
blockchain.polygon.rpc-url=${POLYGON_RPC_URL}
blockchain.badge.contract-address=${BADGE_CONTRACT_ADDRESS}
```

---

## Frontend Integration

### Example: Call `createBadge` via ethers

```javascript
import { ethers } from 'ethers';

const badgeContractAddress = '0xC5e...';
const badgeABI = [
  {
    "inputs": [
      { "name": "to", "type": "address" },
      { "name": "metadata", "type": "string" }
    ],
    "name": "createBadge",
    "outputs": [],
    "stateMutability": "nonpayable",
    "type": "function"
  }
];

async function createBadgeAndNotifyBackend() {
  const provider = new ethers.BrowserProvider(window.ethereum);
  const signer = await provider.getSigner();
  
  const contract = new ethers.Contract(badgeContractAddress, badgeABI, signer);
  
  const toAddress = '0x...';
  const metadata = JSON.stringify({ name: 'Achievement', rarity: 'rare' });
  
  // Send transaction (signed by MetaMask)
  const tx = await contract.createBadge(toAddress, metadata);
  const txHash = tx.hash;
  
  // Notify backend with txHash
  const response = await fetch('/api/blockchain/badges', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      txHash,
      toAddress,
      metadata,
      chainId: 137
    })
  });
  
  const badge = await response.json();
  console.log('Badge registered:', badge);
}
```

---

## Testing

### Local Hardhat (Mumbai or Localhost)

```bash
cd blockchain-contracts

# Compile
npx hardhat compile

# Deploy to Mumbai testnet
MUMBAI_RPC_URL=... DEPLOYER_PRIVATE_KEY=... npx hardhat run scripts/deploy.js --network mumbai

# Or localhost (run hardhat node in another terminal)
npx hardhat run scripts/deploy.js --network localhost
```

### Polygon Mainnet (Production)

```bash
cd blockchain-contracts

# Deploy via Railway (uses Railway secrets)
railway run npx hardhat run --network polygon scripts/deploy.js
```

---

## Monitoring & Troubleshooting

### Check Badge Counter
```bash
# Call getNextId() on the contract
curl -X POST https://polygon-mainnet.g.alchemy.com/v2/YOUR_KEY \
  -H 'Content-Type: application/json' \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "eth_call",
    "params": [{
      "to": "0xC5e...",
      "data": "0x..."
    }, "latest"]
  }'
```

### View Transaction on Polygonscan
```
https://polygonscan.com/tx/0xTXHASH
```

### Common Errors

| Error | Cause | Fix |
|-------|-------|-----|
| "Badge contract address not configured" | `BADGE_CONTRACT_ADDRESS` not set | Set env var in Railway |
| "Invalid chainId" | Request sent wrong chain | Ensure chainId=137 |
| "Transaction failed on-chain" | Metadata too long or gas limit | Reduce metadata size |
| "Transaction not confirmed" | RPC slow or tx reverted | Retry or check Polygonscan |

---

## Security Considerations

1. **Private Keys:** Never store deployer key in git or `.env` files. Use Railway/GitHub Secrets only.
2. **Chain Validation:** Always check chainId before processing (prevent accidental testnet tx on mainnet).
3. **Metadata Limits:** Keep metadata ≤ 1KB to avoid exceeding Polygon gas limits.
4. **Idempotency:** Same txHash won't be registered twice (unique constraint on `hashTransacao`).
5. **Logging:** Never log full private keys; mask sensitive data with `XXX` in logs.

---

## Future Enhancements

- [ ] Add event listener to consume `BadgeCreated` events from Polygon
- [ ] Cache contract ABI in backend
- [ ] Implement batch badge creation
- [ ] Add role-based access control (only admins can mint)
- [ ] Support badge transfers and delegation
- [ ] Integrate with TheGraph for GraphQL queries

---

**Last Updated:** September 13, 2026  
**Maintainer:** Mifica Team
