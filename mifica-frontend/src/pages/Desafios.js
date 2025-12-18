import React, { useEffect, useState } from 'react';
import api from '../services/api';

function Desafios() {
  const [desafios, setDesafios] = useState([]);
  const [novoDesafio, setNovoDesafio] = useState({
    titulo: '',
    descricao: '',
    pontos: 0
  });

  useEffect(() => {
    api.get('/desafios')
      .then(res => setDesafios(res.data))
      .catch(err => console.error("Erro ao buscar desafios:", err));
  }, []);

  const criarDesafio = () => {
    api.post('/desafios', novoDesafio)
      .then(res => {
        setDesafios([...desafios, res.data]);
        setNovoDesafio({ titulo: '', descricao: '', pontos: 0 });
      })
      .catch(err => console.error("Erro ao criar desafio:", err));
  };

  return (
    <div>
      <h2>Desafios Gamificados</h2>

      <input
        type="text"
        placeholder="Título"
        value={novoDesafio.titulo}
        onChange={e => setNovoDesafio({ ...novoDesafio, titulo: e.target.value })}
      />
      <input
        type="text"
        placeholder="Descrição"
        value={novoDesafio.descricao}
        onChange={e => setNovoDesafio({ ...novoDesafio, descricao: e.target.value })}
      />
      <input
        type="number"
        placeholder="Pontos"
        value={novoDesafio.pontos}
        onChange={e => setNovoDesafio({ ...novoDesafio, pontos: parseInt(e.target.value) })}
      />
      <button onClick={criarDesafio}>Criar Desafio</button>

      <ul>
        {desafios.map(d => (
          <li key={d.id}>
            <strong>{d.titulo}</strong> — {d.descricao} ({d.pontos} pontos)
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Desafios;
