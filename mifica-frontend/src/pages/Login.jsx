import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import useMediaQuery from '../hooks/useMediaQuery';
import MobileMenuCadastro from '../components/MobileMenuCadastro';
import ModalEmailNaoVerificado from '../components/ModalEmailNaoVerificado';
import logo from '../assets/logo.png';
import { jwtDecode } from 'jwt-decode';

export default function Login() {
  const [token, setToken] = useState('');
  const [emailNaoVerificado, setEmailNaoVerificado] = useState(false);
  const [emailGuardado, setEmailGuardado] = useState('');
  const [modalAberto, setModalAberto] = useState(false);
  const { login } = useAuth();
  const { isMobile } = useMediaQuery();
  const navigate = useNavigate();
  const location = useLocation();

  const origem = location.state?.from || '/dashboard';

  const handleLogin = async (e) => {
    e.preventDefault();

    // ✅ Captura valores diretamente do formulário
    const formData = new FormData(e.target);
    const emailForm = formData.get("email");
    const senhaForm = formData.get("senha");

    try {
      const res = await api.post('/usuarios/login', {
        email: emailForm.trim(),
        senha: senhaForm.trim()
      });

      const tokenRecebido = res.data.token;
      if (!tokenRecebido) {
        alert("Login falhou: token não recebido.");
        return;
      }

      // ✅ Verifica se o email foi verificado
      const emailVerificado = res.data.emailVerificado;
      
      if (!emailVerificado) {
        // Email não verificado - mostra modal e guarda dados
        setEmailGuardado(emailForm);
        setEmailNaoVerificado(true);
        setModalAberto(true);
        return;
      }

      const decoded = jwtDecode(tokenRecebido);

      const usuario = {
        token: tokenRecebido,
        id: res.data.id, // ✅ garante que o ID seja salvo
        nome: res.data.nome || decoded.nome || 'Usuário',
        email: res.data.email || decoded.sub || emailForm,
        reputacao: res.data.reputacao || 0,
        conquistas: res.data.conquistas || [],
        role: res.data.role || decoded.role
      };

      // ✅ Salva no localStorage
      localStorage.setItem('token', tokenRecebido);
      localStorage.setItem('usuario', JSON.stringify(usuario));

      login(usuario);
      setToken(tokenRecebido);
      navigate(origem);
    } catch (err) {
      console.error("Erro ao fazer login:", err);
      alert("Credenciais inválidas ou erro de conexão.");
    }
  };

  const handleReenviarEmail = async () => {
    if (!emailGuardado) {
      alert("Erro: e-mail não encontrado.");
      return;
    }

    try {
      const response = await api.post('/usuarios/reenviar-confirmacao', {
        email: emailGuardado
      });
      alert(response.data || "E-mail de confirmação reenviado com sucesso!");
      setEmailNaoVerificado(false);
      setModalAberto(false);
      setEmailGuardado('');
    } catch (err) {
      console.error("Erro ao reenviar email:", err);
      alert("Não foi possível reenviar o e-mail de confirmação agora.");
    }
  };

  return (
    <>
      <MobileMenuCadastro />
      <ModalEmailNaoVerificado 
        isOpen={modalAberto} 
        onClose={() => setModalAberto(false)} 
      />

      <div className={`min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 px-3 md:px-4 ${isMobile ? 'py-8' : 'py-4'}`}>
        <form
          onSubmit={handleLogin}
          className="bg-white rounded-xl shadow-lg p-6 md:p-8 w-full max-w-md border border-gray-200"
        >
          <div className="flex flex-col items-center mb-7 md:mb-8">
            <img src={logo} alt="Logo Mifica" className="w-28 md:w-32 mb-3 md:mb-4 drop-shadow-lg" />
            <h2 className="text-3xl md:text-5xl font-extrabold tracking-tight text-blue-700 text-center">Software que transforma</h2>
            <p className="text-sm md:text-base text-gray-500 text-center">Acesse sua conta para continuar</p>
          </div>

          <div className="space-y-3 md:space-y-4">
            <input
              type="email"
              name="email" // ✅ necessário para FormData
              placeholder="Email"
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            
            {/* Botão Reenviar Email - Protegido */}
            {emailNaoVerificado && !modalAberto && (
              <button
                type="button"
                onClick={handleReenviarEmail}
                className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-orange-400 bg-orange-50 text-orange-600 rounded-md font-semibold hover:bg-orange-100 transition"
              >
                ✉️ Reenviar email de confirmação
              </button>
            )}
            
            <input
              type="password"
              name="senha" // ✅ necessário para FormData
              placeholder="Senha"
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <button
            type="submit"
            className="mt-4 md:mt-6 w-full bg-blue-600 text-white py-2 text-sm md:text-base rounded-md font-semibold hover:bg-blue-700 transition"
          >
            Entrar
          </button>

          <button
            type="button"
            onClick={() => navigate('/cadastro')}
            className="mt-2 md:mt-3 w-full bg-gray-100 text-gray-700 py-2 text-sm md:text-base rounded-md font-semibold hover:bg-gray-200 transition"
          >
            Criar conta
          </button>

          {token && (
            <div className="mt-6">
              <h3 className="text-sm font-semibold mb-2">Token JWT:</h3>
              <textarea
                value={token}
                readOnly
                rows={4}
                className="w-full border rounded p-2 text-xs"
              />
            </div>
          )}
        </form>
      </div>
    </>
  );
}
