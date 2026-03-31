// src/pages/Cadastro.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import useMediaQuery from '../hooks/useMediaQuery';
import MobileMenuCadastro from '../components/MobileMenuCadastro';
import logo from '../assets/logo.png';

export default function Cadastro() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [mensagemCampoSenha, setMensagemCampoSenha] = useState('');
  const [erroCadastro, setErroCadastro] = useState(false);
  const navigate = useNavigate();
  const { isMobile } = useMediaQuery();

  const handleCadastro = async (e) => {
    e.preventDefault();
    setMensagemCampoSenha('');
    setErroCadastro(false);

    try {
      const dados = { nome, email, senha };

      // ✅ chamada usando baseURL já configurada em api.js
      const response = await api.post('/usuarios/cadastro', dados);

      console.log("Cadastro realizado:", response.data);
      setMensagemCampoSenha('Usuario criada com sucesso!');
      setErroCadastro(false);
      setNome('');
      setEmail('');
      setSenha('');
    } catch (error) {
      console.error("Erro ao cadastrar:", error);
      const msg = error?.response?.data || "Erro ao cadastrar usuário";

      if (typeof msg === 'string') {
        const msgLower = msg.toLowerCase();
        if (msgLower.includes('email já cadastrado') || msgLower.includes('conta já existe')) {
          setMensagemCampoSenha('E-mail já cadastrado!');
          setErroCadastro(true);
          return;
        }
      }

      setMensagemCampoSenha('Não foi possível concluir o cadastro agora.');
      setErroCadastro(true);
    }
  };

  return (
    <>
      {/* Menu Mobile */}
      <MobileMenuCadastro />

      <div className={`min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 px-3 md:px-4 ${isMobile ? 'py-8' : 'py-4'}`}>
        <form
          onSubmit={handleCadastro}
          className="bg-white rounded-xl shadow-lg p-6 md:p-8 w-full max-w-md border border-gray-200"
        >
        <div className="flex flex-col items-center mb-7 md:mb-8">
          <img src={logo} alt="Logo Mifica" className="w-28 md:w-32 mb-3 md:mb-4 drop-shadow-lg" />
          <h2 className="text-3xl md:text-5xl font-extrabold tracking-tight text-blue-700 text-center">Software que transforma</h2>
          <p className="text-sm md:text-base text-gray-500 text-center">Crie sua conta para começar</p>
        </div>

        <div className="space-y-3 md:space-y-4">
          <input
            type="text"
            placeholder="Nome completo"
            value={nome}
            onChange={e => setNome(e.target.value)}
            required
            className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="password"
            placeholder="Senha"
            value={senha}
            onChange={e => setSenha(e.target.value)}
            required
            className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {mensagemCampoSenha && (
            <p className={`text-xs md:text-sm mt-1 ${erroCadastro ? 'text-red-600' : 'text-green-700'}`}>
              {mensagemCampoSenha}
            </p>
          )}
        </div>

        <button
          type="submit"
          className="mt-4 md:mt-6 w-full bg-blue-600 text-white py-2 text-sm md:text-base rounded-md font-semibold hover:bg-blue-700 transition"
        >
          Cadastrar
        </button>

        <button
          type="button"
          onClick={() => navigate('/login')}
          className="mt-2 md:mt-3 w-full bg-gray-100 text-gray-700 py-2 text-sm md:text-base rounded-md font-semibold hover:bg-gray-200 transition"
        >
          Ir para login
        </button>
      </form>
    </div>
    </>
  );
}
