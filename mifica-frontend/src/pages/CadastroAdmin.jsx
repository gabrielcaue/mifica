// src/pages/CadastroAdmin.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import logo from '../assets/logo.png';

export default function CadastroAdmin() {
  const [senhaAcesso, setSenhaAcesso] = useState('');
  const [autenticado, setAutenticado] = useState(false);

  const senhaAdminCorreta = import.meta.env.VITE_SENHA_ADMIN;

  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [telefone, setTelefone] = useState('');
  const [dataNascimento, setDataNascimento] = useState('');
  const [role, setRole] = useState('USER');
  const navigate = useNavigate();

  const formatarDataParaISO = (dataBR) => {
    const [dia, mes, ano] = dataBR.split('/');
    return `${ano}-${mes}-${dia}`;
  };

  const handleCadastro = async (e) => {
    e.preventDefault();
    try {
      const dataFormatada = formatarDataParaISO(dataNascimento);

      await api.post('/usuarios/cadastro-admin', {
        nome,
        email,
        senha,
        telefone,
        dataNascimento: dataFormatada,
        role,
        senhaAcesso
      });

      alert('Administrador cadastrado com sucesso!');
      setNome('');
      setEmail('');
      setSenha('');
      setTelefone('');
      setDataNascimento('');
      setRole('USER');
      navigate('/login');
    } catch (err) {
      console.error('Erro ao cadastrar administrador:', err);
      alert('Erro ao cadastrar administrador');
    }
  };

  if (!autenticado) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white px-4">
        <div className="bg-slate-800 p-8 rounded-xl shadow-xl w-full max-w-md">
          <div className="flex flex-col items-center mb-4">
            <img src={logo} alt="Logo Mifica" className="w-12 mb-3" />
            <h2 className="text-2xl font-bold text-center">🔐 Acesso Restrito</h2>
            <p className="text-sm text-gray-300 text-center">Digite a senha para acessar o cadastro de administrador</p>
          </div>
<form
  onSubmit={(e) => {
    e.preventDefault();
    if (senhaAcesso === senhaAdminCorreta) {
      setAutenticado(true);
    } else {
      alert("Senha incorreta");
    }
  }}
>
  <input
    type="password"
    placeholder="Senha de acesso"
    value={senhaAcesso}
    onChange={e => setSenhaAcesso(e.target.value)}
    className="w-full px-4 py-2 mb-4 border border-gray-600 rounded-md bg-slate-700 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
  />
  <button
    type="submit"
    className="w-full bg-indigo-600 text-white py-2 rounded-md font-semibold hover:bg-indigo-700 transition"
  >
    Entrar
  </button>
</form>

        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 px-4">
      <form
        onSubmit={handleCadastro}
        className="bg-white rounded-xl shadow-xl p-10 w-full max-w-lg border border-gray-300"
      >
        <div className="flex flex-col items-center mb-6">
          <img src={logo} alt="Logo Mifica" className="w-12 mb-3" />
          <h2 className="text-3xl font-bold text-gray-800">Cadastro de Administrador</h2>
          <p className="text-sm text-gray-500">Preencha os dados para criar uma conta administrativa</p>
        </div>

        <div className="space-y-4">
          <input
            type="text"
            placeholder="Nome completo"
            value={nome}
            onChange={e => setNome(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <input
            type="email"
            placeholder="Email institucional"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <input
            type="password"
            placeholder="Senha segura"
            value={senha}
            onChange={e => setSenha(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <input
            type="text"
            placeholder="Telefone"
            value={telefone}
            onChange={e => setTelefone(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <input
            type="text"
            placeholder="Data de nascimento (dd/MM/yyyy)"
            value={dataNascimento}
            onChange={e => setDataNascimento(e.target.value)}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <select
            value={role}
            onChange={e => setRole(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="USER">Usuário</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>

        <button
          type="submit"
          className="mt-6 w-full bg-indigo-600 text-white py-2 rounded-md font-semibold hover:bg-indigo-700 transition"
        >
          Cadastrar Administrador
        </button>
      </form>
    </div>
  );
}
