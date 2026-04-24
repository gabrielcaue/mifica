import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

function TransacoesList({ refreshKey = 0 }) {
  const [transacoes, setTransacoes] = useState([]);
  const { token } = useAuth();

  useEffect(() => {
    if (!token) return;

    api.get('/transacoes')
      .then(response => setTransacoes(response.data))
      .catch(error => console.error('Erro ao buscar transações:', error));
  }, [token, refreshKey]);

  return (
    <div>
      <h2>Transações Blockchain</h2>
      <ul>
        {transacoes.map(tx => (
          <li key={tx.id}>
            {tx.remetente} → {tx.destinatario} | R$ {tx.valor} | {tx.dataTransacao}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransacoesList;
