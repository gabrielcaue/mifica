import React, { useState } from 'react';
import api from '../services/api';

function RegistrarTransacao() {
  const [form, setForm] = useState({
    hashTransacao: '',
    remetente: '',
    destinatario: '',
    valor: '',
  });

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = e => {
    e.preventDefault();
    api.post('/blockchain/transacoes', form)
      .then(() => alert('Transação registrada com sucesso!'))
      .catch(error => console.error('Erro ao registrar transação:', error));
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="hashTransacao" placeholder="Hash" onChange={handleChange} />
      <input name="remetente" placeholder="Remetente" onChange={handleChange} />
      <input name="destinatario" placeholder="Destinatário" onChange={handleChange} />
      <input name="valor" placeholder="Valor" type="number" onChange={handleChange} />
      <button type="submit">Registrar</button>
    </form>
  );
}

export default RegistrarTransacao;
