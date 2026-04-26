import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

// Função para formatar valores em Real com as regras especificadas
const formatarReal = (valor) => {
  if (valor === null || valor === undefined || isNaN(valor)) return 'R$ 0';
  
  // Converter para inteiro (ignorar centavos)
  const valorInteiro = Math.floor(valor);
  
  // Formatar com ponto a partir de 1000
  const valorFormatado = valorInteiro.toLocaleString('pt-BR');
  
  return `R$ ${valorFormatado}`;
};

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
            {tx.remetente} → {tx.destinatario} | {formatarReal(tx.valor)} | {tx.dataTransacao}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransacoesList;
