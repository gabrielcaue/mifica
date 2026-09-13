import React, { useState } from 'react';
import api from '../services/api';

/**
 * Componente para criar badges via contrato inteligente Badge no Polygon.
 * 
 * Fluxo:
 * 1. Usuário preenche endereço destinatário e metadata
 * 2. Constrói transação: Badge.createBadge(toAddress, metadata)
 * 3. MetaMask assina transação
 * 4. Envia txHash para backend (POST /api/blockchain/badges)
 * 5. Backend polling no RPC até confirmação e persiste no BD
 */
function CriarBadge() {
  const [form, setForm] = useState({
    toAddress: '',
    metadata: '',
  });
  
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      // Validar MetaMask
      if (!window.ethereum) {
        throw new Error('MetaMask não encontrado no navegador.');
      }

      // Solicitar contas
      const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
      const from = accounts[0];

      // Validar endereço destinatário
      const toAddress = form.toAddress.trim();
      if (!toAddress.match(/^0x[a-fA-F0-9]{40}$/)) {
        throw new Error('Endereço inválido. Deve ser um endereço Ethereum válido (0x...).');
      }

      // Validar metadata
      const metadata = form.metadata.trim();
      if (!metadata) {
        throw new Error('Metadata é obrigatória.');
      }
      if (metadata.length > 1000) {
        throw new Error('Metadata não pode exceder 1000 caracteres.');
      }

      // Garantir que está na rede Polygon (chainId 137 = 0x89)
      try {
        await window.ethereum.request({
          method: 'wallet_switchEthereumChain',
          params: [{ chainId: '0x89' }],
        });
      } catch (switchError) {
        console.warn('Falha ao mudar rede:', switchError);
        // Continuar mesmo se falhar (pode ser que já esteja na rede correta)
      }

      // ABI do contrato Badge - apenas a função createBadge
      const badgeABI = [
        {
          inputs: [
            { name: 'to', type: 'address' },
            { name: 'metadata', type: 'string' }
          ],
          name: 'createBadge',
          outputs: [],
          stateMutability: 'nonpayable',
          type: 'function'
        }
      ];

      // Endereço do contrato Badge (obtém do backend ou usa valor hardcoded)
      const badgeContractAddress = process.env.REACT_APP_BADGE_CONTRACT_ADDRESS 
        || '0xC5e...'; // Placeholder - será preenchido pelo backend

      if (badgeContractAddress.includes('C5e')) {
        console.warn('Aviso: BADGE_CONTRACT_ADDRESS não configurado. Use variável de ambiente REACT_APP_BADGE_CONTRACT_ADDRESS');
      }

      // Usar ethers se disponível (recomendado)
      let txHash;
      if (window.ethers) {
        const provider = new window.ethers.BrowserProvider(window.ethereum);
        const signer = await provider.getSigner();
        const contract = new window.ethers.Contract(badgeContractAddress, badgeABI, signer);

        const tx = await contract.createBadge(toAddress, metadata);
        txHash = tx.hash;
      } else {
        // Fallback: construir manualmente o call data
        throw new Error('ethers.js não disponível. Adicione-o ao HTML ou instale via npm.');
      }

      console.log('Transação assinada. TxHash:', txHash);

      // Enviar para backend para validação e persistência
      const response = await api.post('/api/blockchain/badges', {
        txHash,
        toAddress,
        metadata,
        chainId: 137
      });

      setResult({
        txHash,
        badgeId: response.data?.id,
        message: 'Badge criado com sucesso! Transação confirmada na blockchain.'
      });

      // Limpar formulário
      setForm({ toAddress: '', metadata: '' });

    } catch (err) {
      console.error('Erro ao criar badge:', err);
      setError(
        err?.message || 
        err?.data?.message || 
        'Erro ao criar badge. Verifique o console para detalhes.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto p-4 bg-gray-800 rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold text-white mb-4">Criar Badge</h2>

      {error && (
        <div className="bg-red-600 text-white p-3 rounded mb-4">
          <p className="font-semibold">Erro:</p>
          <p>{error}</p>
        </div>
      )}

      {result && (
        <div className="bg-green-600 text-white p-3 rounded mb-4">
          <p className="font-semibold">✅ Sucesso!</p>
          <p>{result.message}</p>
          <p className="text-sm mt-2">
            <strong>TxHash:</strong> <code className="bg-black px-2 py-1 rounded">{result.txHash.substring(0, 20)}...</code>
          </p>
          {result.badgeId && (
            <p className="text-sm mt-1">
              <strong>Badge ID:</strong> {result.badgeId}
            </p>
          )}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-gray-300 mb-2">Endereço do Destinatário</label>
          <input
            type="text"
            name="toAddress"
            value={form.toAddress}
            onChange={handleChange}
            placeholder="0x..."
            className="w-full px-3 py-2 bg-gray-700 text-white rounded border border-gray-600 focus:outline-none focus:border-blue-500"
            disabled={loading}
          />
          <p className="text-xs text-gray-400 mt-1">Deve ser um endereço Ethereum válido (0x...)</p>
        </div>

        <div>
          <label className="block text-gray-300 mb-2">Metadata</label>
          <textarea
            name="metadata"
            value={form.metadata}
            onChange={handleChange}
            placeholder="JSON com dados do badge, ex: {&quot;name&quot;: &quot;Achievement&quot;, &quot;rarity&quot;: &quot;rare&quot;}"
            rows={3}
            className="w-full px-3 py-2 bg-gray-700 text-white rounded border border-gray-600 focus:outline-none focus:border-blue-500 text-sm"
            disabled={loading}
          />
          <p className="text-xs text-gray-400 mt-1">Máximo 1000 caracteres</p>
          <p className="text-xs text-gray-500 mt-1">{form.metadata.length} / 1000</p>
        </div>

        <button
          type="submit"
          disabled={loading || !form.toAddress || !form.metadata}
          className={`w-full py-2 px-4 rounded font-semibold transition ${
            loading || !form.toAddress || !form.metadata
              ? 'bg-gray-600 text-gray-400 cursor-not-allowed'
              : 'bg-blue-600 text-white hover:bg-blue-700'
          }`}
        >
          {loading ? '⏳ Processando...' : '🚀 Criar Badge'}
        </button>
      </form>

      <div className="mt-4 p-3 bg-gray-700 rounded text-xs text-gray-300">
        <p className="font-semibold mb-1">ℹ️ Informações:</p>
        <ul className="list-disc list-inside space-y-1">
          <li>MetaMask é necessário para assinar a transação</li>
          <li>Você deve estar conectado à rede Polygon (chainId: 137)</li>
          <li>A transação será confirmada automaticamente no blockchain</li>
          <li>O backend persistirá o badge após confirmação</li>
        </ul>
      </div>
    </div>
  );
}

export default CriarBadge;
