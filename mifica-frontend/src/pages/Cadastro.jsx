// src/pages/Cadastro.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import logo from '../assets/logo.png';

export default function Cadastro() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const navigate = useNavigate();

  const handleCadastro = async (e) => {
    e.preventDefault();
    try {
      // 🔧 ALTERADO: antes estava "/api/usuarios/cadastro"
      // agora basta "/usuarios/cadastro" porque o baseURL já é "/api"
      const response = await api.post('/usuarios/cadastro', {
        nome,
        email,
        senha,
      });

      console.log('Cadastro realizado:', response.data);
      navigate('/login');
    } catch (error) {
      console.error('Erro ao cadastrar:', error);
      alert('Erro ao cadastrar usuário');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 px-4">
      <form
        onSubmit={handleCadastro}
        className="bg-white rounded-xl shadow-lg p-8 w-full max-w-md border border-gray-200"
      >
        <div className="flex flex-col items-center mb-6">
          <img src={logo} alt="Logo Mifica" className="w-14 mb-3" />
          <h2 className="text-2xl font-bold text-gray-800">Cadastro Mifica</h2>
          <p className="text-sm text-gray-500">Crie sua conta para começar</p>
        </div>

        <div className="space-y-4">
          <input
            type="text"
            placeholder="Nome completo"
            value={nome}
            onChange={e => setNome(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="password"
            placeholder="Senha"
            value={senha}
            onChange={e => setSenha(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <button
          type="submit"
          className="mt-6 w-full bg-blue-600 text-white py-2 rounded-md font-semibold hover:bg-blue-700 transition"
        >
          Cadastrar
        </button>
      </form>
    </div>
  );
}
