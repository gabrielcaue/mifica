// src/pages/AcessoAdmin.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../assets/logo.png';

export default function AcessoAdmin() {
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

const senhaCorreta = import.meta.env.VITE_SENHA_ADMIN;

const handleSubmit = (e) => {
  e.preventDefault();
  if (senha === senhaCorreta) {
    localStorage.setItem('acessoAdmin', 'true');
    navigate('/cadastro-admin');
  } else {
    setErro('Senha incorreta. Tente novamente.');
  }
};


  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 px-4">
      <form
        onSubmit={handleSubmit}
        className="bg-white rounded-xl shadow-lg p-8 w-full max-w-md border border-gray-200"
      >
        <div className="flex flex-col items-center mb-6">
          <img src={logo} alt="Logo Mifica" className="w-14 mb-3" />
          <h2 className="text-2xl font-bold text-gray-800">Acesso Administrativo</h2>
          <p className="text-sm text-gray-500">Digite a senha para continuar</p>
        </div>

        <input
          type="password"
          placeholder="Senha de acesso"
          value={senha}
          onChange={e => setSenha(e.target.value)}
          required
          className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
        />

        {erro && <p className="text-red-600 text-sm mt-2">{erro}</p>}

        <button
          type="submit"
          className="mt-6 w-full bg-red-600 text-white py-2 rounded-md font-semibold hover:bg-red-700 transition"
        >
          Entrar
        </button>
      </form>
    </div>
  );
}
