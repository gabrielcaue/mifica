# Frontend - Badge Integration

## Configuração

### 1. Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto frontend:

```bash
# Badge Contract Address (substitua com o endereço deployado)
REACT_APP_BADGE_CONTRACT_ADDRESS=0xC5e...

# API Backend
REACT_APP_API_URL=http://localhost:8080/api
# ou em produção:
# REACT_APP_API_URL=https://mifica-backend.up.railway.app/api
```

### 2. Dependências do Frontend

Certifique-se de que você tem `ethers.js` disponível. Se não estiver instalado:

```bash
cd mifica-frontend
npm install ethers
```

Ou adicione via CDN no `index.html`:

```html
<script src="https://cdn.ethers.io/lib/ethers-5.umd.min.js"></script>
```

### 3. Componente CriarBadge

Use o novo componente `CriarBadge.jsx` em sua página:

```jsx
import CriarBadge from './components/CriarBadge';

function MyPage() {
  return (
    <div>
      <CriarBadge />
    </div>
  );
}

export default MyPage;
```

## Fluxo de Funcionamento

### Criação de Badge (End-to-End)

```
User Interface (React)
        ↓
   1. Input: toAddress + metadata
   2. Validação de endereço (regex 0x...)
   3. Muda rede para Polygon 137 (MetaMask)
   4. Chama contrato: Badge.createBadge(toAddress, metadata)
   5. MetaMask abre popup para assinar
   6. Após assinatura, obtém txHash
        ↓
Backend API (Spring Boot)
        ↓
   POST /api/blockchain/badges
   Body: {
     "txHash": "0x...",
     "toAddress": "0x...",
     "metadata": "...",
     "chainId": 137
   }
        ↓
   1. Valida chainId (deve ser 137)
   2. Verifica idempotência (txHash já existe?)
   3. Polling no RPC (até 60 tentativas, 5s cada)
   4. Aguarda confirmação da transação
   5. Se confirmada, persiste em BD
   6. Retorna badge com ID
        ↓
User Interface (React)
        ↓
   Exibe mensagem de sucesso com TxHash e Badge ID
   Link para Polygonscan: https://polygonscan.com/tx/{txHash}
```

## Tratamento de Erros

O componente `CriarBadge.jsx` trata erros comuns:

| Erro | Causa | Solução |
|------|-------|---------|
| "MetaMask não encontrado" | Extensão não instalada | Instale MetaMask |
| "Endereço inválido" | Formato errado | Use formato 0x40 caracteres |
| "Metadata obrigatória" | Campo vazio | Preencha metadata |
| "Falha ao mudar rede" | Usuário não tem Polygon | Adicione rede no MetaMask |
| "Transação não confirmada" | RPC lento ou falha | Retry manual |

## Monitoramento

### Ver transações no Polygonscan

```
https://polygonscan.com/tx/{txHash}
https://polygonscan.com/address/{BADGE_CONTRACT_ADDRESS}
```

### Ver badges no backend

```bash
curl -H "Authorization: Bearer {JWT_TOKEN}" \
  http://localhost:8080/api/blockchain/transacoes
```

## Desenvolvimento Local

### Testar com Hardhat (localhost)

1. Em `blockchain-contracts/`, rodar Hardhat node:
```bash
npx hardhat node
```

2. Deploy local:
```bash
HARDHAT_NETWORK=hardhat npx hardhat run scripts/deploy.js --network localhost
```

3. Atualizar `.env.local`:
```bash
REACT_APP_BADGE_CONTRACT_ADDRESS=<endereço deployado localmente>
REACT_APP_API_URL=http://localhost:8080/api
```

### Testar com Mumbai Testnet

1. Deploy:
```bash
MUMBAI_RPC_URL=... DEPLOYER_PRIVATE_KEY=... npx hardhat run scripts/deploy.js --network mumbai
```

2. Atualizar `.env.local`:
```bash
REACT_APP_BADGE_CONTRACT_ADDRESS=<endereço mumbai>
```

3. No MetaMask: trocar para Mumbai testnet

## Security Considerations

- ✅ Nunca armazene private keys no frontend
- ✅ MetaMask gerencia todas as chaves privadas
- ✅ Validação de chainId previne txs em rede errada
- ✅ Backend valida e persiste apenas txs confirmadas
- ✅ CORS protege endpoints da API

## Troubleshooting

### "ethers.js não disponível"

Solução: Instale ou adicione via CDN:
```bash
npm install ethers
```

### Transação fica pendente

Verificar:
1. Saldo de MATIC (precisa de gas)
2. Status no Polygonscan
3. Se backend está rodando (`/health` endpoint)

### CORS error ao chamar backend

Solução: Verificar que `REACT_APP_API_URL` está correto e CORS está ativado no backend.

---

**Last Updated:** September 13, 2026
