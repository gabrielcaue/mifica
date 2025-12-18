import React, { useEffect, useState } from 'react';
import api from '../services/api';

function Contratos() {
  const [contratos, setContratos] = useState([]);
  const [novoContrato, setNovoContrato] = useState({
    nome: '',
    descricao: '',
    enderecoBlockchain: ''
  });

  useEffect(() => {
    api.get('/contratos')
      .then(res => setContratos(res.data))
      .catch(err => console.error("Erro ao buscar contratos:", err));
  }, []);

  const criarContrato = () => {
    api.post('/contratos', novoContrato)
      .then(res => {
        setContratos([...contratos, res.data]);
        setNovoContrato({ nome: '', descricao: '', enderecoBlockchain: '' });
      })
      .catch(err => console.error("Erro ao criar contrato:", err));
  };

  return (
    <div>
      <h2>Contratos Inteligentes</h2>

      <input
        type="text"
        placeholder="Nome"
        value={novoContrato.nome}
        onChange={e => setNovoContrato({ ...novoContrato, nome: e.target.value })}
      />
      <input
        type="text"
        placeholder="Descrição"
        value={novoContrato.descricao}
        onChange={e => setNovoContrato({ ...novoContrato, descricao: e.target.value })}
      />
      <input
        type="text"
        placeholder="Endereço Blockchain"
        value={novoContrato.enderecoBlockchain}
        onChange={e => setNovoContrato({ ...novoContrato, enderecoBlockchain: e.target.value })}
      />
      <button onClick={criarContrato}>Criar Contrato</button>

      <ul>
        {contratos.map(c => (
          <li key={c.id}>
            <strong>{c.nome}</strong> - {c.descricao} ({c.enderecoBlockchain})
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Contratos;
