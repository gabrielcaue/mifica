import React, { useState } from 'react';
import api from '../services/api';

function RegistrarTransacao() {
  const [form, setForm] = useState({
    destinatario: '',
    valor: '',
  });

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = e => {
    e.preventDefault();

    const sendWithMetaMask = async () => {
      try {
        if (!window.ethereum) {
          alert('MetaMask não encontrado no navegador.');
          return;
        }

        // Solicita contas e usa a primeira
        const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
        const from = accounts[0];

        const to = form.destinatario;
        const valueEth = Number(form.valor || 0);
        if (!to || valueEth <= 0) {
          alert('Informe destinatário e valor válidos.');
          return;
        }

        // Converter ETH -> wei (pode perder precisão em floats muito grandes)
        const wei = BigInt(Math.round(valueEth * 1e18)).toString(16);
        const valueHex = '0x' + wei;

        // Garante que a rede é a Polygon mainnet (chainId 137 = 0x89)
        try {
          await window.ethereum.request({
            method: 'wallet_switchEthereumChain',
            params: [{ chainId: '0x89' }],
          });
        } catch (switchError) {
          // Usuário pode não ter a rede adicionada; MetaMask pode pedir para adicionar.
          console.warn('Falha ao mudar de rede:', switchError);
        }

        const txParams = {
          from,
          to,
          value: valueHex,
        };

        const txHash = await window.ethereum.request({ method: 'eth_sendTransaction', params: [txParams] });

        // Envia para backend para validação/persistência de receipt
        await api.post('/blockchain/transacoes', {
          txHash,
          from,
          to,
          value: valueEth,
          chainId: 137
        });

        alert('Transação enviada. TxHash: ' + txHash);
        setForm({ destinatario: '', valor: '' });
      } catch (err) {
        console.error('Erro ao enviar transação:', err);
        alert('Erro ao enviar transação: ' + (err?.message || err));
      }
    };

    sendWithMetaMask();
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="destinatario" placeholder="Destinatário" onChange={handleChange} />
      <input name="valor" placeholder="Valor" type="number" onChange={handleChange} />
      <button type="submit">Registrar</button>
    </form>
  );
}

export default RegistrarTransacao;
