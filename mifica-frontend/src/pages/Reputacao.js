import React, { useEffect, useState } from 'react';
import api from '../services/api';

function Reputacao() {
  const [email, setEmail] = useState('');
  const [historico, setHistorico] = useState([]);

  const buscarHistorico = () => {
    api.get(`/reputacao?email=${email}`)
      .then(res => setHistorico(res.data))
      .catch(err => console.error("Erro ao buscar histórico:", err));
  };

  return (
    <div>
      <h2>Histórico de Reputação</h2>

      <input
        type="email"
        placeholder="Email do usuário"
        value={email}
        onChange={e => setEmail(e.target.value)}
      />
      <button onClick={buscarHistorico}>Buscar</button>

      <ul>
        {historico.map(h => (
          <li key={h.id}>
            <strong>{h.emailUsuario}</strong> — {h.reputacaoAnterior} ➡️ {h.reputacaoNova} em {new Date(h.dataAlteracao).toLocaleString()}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Reputacao;
