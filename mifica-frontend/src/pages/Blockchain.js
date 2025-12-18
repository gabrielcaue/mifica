import React, { useEffect, useState } from 'react';
import api from '../services/api';

function Blockchain() {
  const [transacoes, setTransacoes] = useState([]);
  const [novaTransacao, setNovaTransacao] = useState({
    hashTransacao: '',
    remetente: '',
    destinatario: '',
    valor: 0
  });

  useEffect(() => {
    api.get('/blockchain/transacoes')
      .then(res => setTransacoes(res.data))
      .catch(err => console.error("Erro ao buscar transações:", err));
  }, []);

  const registrarTransacao = () => {
    api.post('/blockchain/transacoes', novaTransacao)
      .then(res => {
        setTransacoes([...transacoes, res.data]);
        setNovaTransacao({ hashTransacao: '', remetente: '', destinatario: '', valor: 0 });
      })
      .catch(err => console.error("Erro ao registrar transação:", err));
  };

  return (
    <div>
      <h2>Transações Blockchain</h2>

      <input
        type="text"
        placeholder="Hash da transação"
        value={novaTransacao.hashTransacao}
        onChange={e => setNovaTransacao({ ...novaTransacao, hashTransacao: e.target.value })}
      />
      <input
        type="text"
        placeholder="Remetente"
        value={novaTransacao.remetente}
        onChange={e => setNovaTransacao({ ...novaTransacao, remetente: e.target.value })}
      />
      <input
        type="text"
        placeholder="Destinatário"
        value={novaTransacao.destinatario}
        onChange={e => setNovaTransacao({ ...novaTransacao, destinatario: e.target.value })}
      />
      <input
        type="number"
        placeholder="Valor"
        value={novaTransacao.valor}
        onChange={e => setNovaTransacao({ ...novaTransacao, valor: parseFloat(e.target.value) })}
      />
      <button onClick={registrarTransacao}>Registrar Transação</button>

      <ul>
        {transacoes.map(tx => (
          <li key={tx.id}>
            <strong>{tx.hashTransacao}</strong> — {tx.remetente} ➡️ {tx.destinatario} ({tx.valor} ETH) em {new Date(tx.dataTransacao).toLocaleString()}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Blockchain;
