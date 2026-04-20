import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

function TransacoesList({ refreshKey = 0 }) {
  // ICP-TOTAL: 1
  // ICP-01: Lista exibe contrato visual enxuto de transação (destinatário e valor).
  const [transacoes, setTransacoes] = useState([]);
  const { token } = useAuth();

  useEffect(() => {
    if (!token) return;

    api.get('/blockchain/transacoes')
      .then(response => setTransacoes(response.data))
      .catch(error => console.error('Erro ao buscar transações:', error));
  }, [token, refreshKey]);

  return (
    <div>
      <h2>Transações Blockchain</h2>
      <ul>
        {transacoes.map(tx => (
          <li key={tx.id}>
            {tx.destinatario} | R$ {tx.valor}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransacoesList;
