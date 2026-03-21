// src/pages/CadastroAdmin.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import useMediaQuery from '../hooks/useMediaQuery';
import MobileMenuAdmin from '../components/MobileMenuAdmin';
import logo from '../assets/logo.png';

export default function CadastroAdmin() {
  const [senhaAcesso, setSenhaAcesso] = useState('');
  const [autenticado, setAutenticado] = useState(false);
  const { isMobile } = useMediaQuery();

  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [telefone, setTelefone] = useState('');
  const [dataNascimento, setDataNascimento] = useState('');
  const [erroData, setErroData] = useState('');
  const [mensagemCampoSenha, setMensagemCampoSenha] = useState('');
  const [role, setRole] = useState('USER');
  const navigate = useNavigate();

  // Formata automaticamente a digitação para DD/MM/AAAA
  const handleDataChange = (e) => {
    let valor = e.target.value.replace(/\D/g, ''); // remove tudo que não é número
    if (valor.length > 8) valor = valor.slice(0, 8);

    // Insere as barras automaticamente
    if (valor.length >= 5) {
      valor = valor.slice(0, 2) + '/' + valor.slice(2, 4) + '/' + valor.slice(4);
    } else if (valor.length >= 3) {
      valor = valor.slice(0, 2) + '/' + valor.slice(2);
    }

    setDataNascimento(valor);
    setErroData('');
  };

  // Valida se a data é real e se a pessoa tem pelo menos 16 anos
  const validarData = (dataBR) => {
    const partes = dataBR.split('/');
    if (partes.length !== 3) return 'Formato inválido. Use DD/MM/AAAA.';

    const dia = parseInt(partes[0], 10);
    const mes = parseInt(partes[1], 10);
    const ano = parseInt(partes[2], 10);

    if (isNaN(dia) || isNaN(mes) || isNaN(ano)) return 'Data contém valores inválidos.';
    if (dia < 1 || dia > 31) return 'Dia inválido (1 a 31).';
    if (mes < 1 || mes > 12) return 'Mês inválido (1 a 12).';

    const anoAtual = new Date().getFullYear();
    if (ano < 1900 || ano > anoAtual) return `Ano inválido (1900 a ${anoAtual}).`;

    // Verifica se a data realmente existe (ex: 31/02 não existe)
    const dataObj = new Date(ano, mes - 1, dia);
    if (dataObj.getDate() !== dia || dataObj.getMonth() !== mes - 1 || dataObj.getFullYear() !== ano) {
      return 'Data inexistente (ex: 31/02 não existe).';
    }

    // Verifica idade mínima de 16 anos
    const hoje = new Date();
    let idade = hoje.getFullYear() - ano;
    const mesAtual = hoje.getMonth() + 1;
    const diaAtual = hoje.getDate();
    if (mesAtual < mes || (mesAtual === mes && diaAtual < dia)) {
      idade--;
    }
    if (idade < 16) return 'É necessário ter pelo menos 16 anos.';

    return '';
  };

  const formatarDataParaISO = (dataBR) => {
    const [dia, mes, ano] = dataBR.split('/');
    return `${ano}-${mes}-${dia}`;
  };

  const handleCadastro = async (e) => {
    e.preventDefault();
    setMensagemCampoSenha('');

    // Valida data antes de enviar
    const erro = validarData(dataNascimento);
    if (erro) {
      setErroData(erro);
      return;
    }

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

      setMensagemCampoSenha('Usuario cadastrado com sucesso!');
      setNome('');
      setEmail('');
      setSenha('');
      setTelefone('');
      setDataNascimento('');
      setRole('USER');
    } catch (err) {
      console.error('Erro ao cadastrar administrador:', err);
      const msg = err?.response?.data || 'Erro ao cadastrar administrador';

      if (typeof msg === 'string') {
        const msgLower = msg.toLowerCase();
        if (msgLower.includes('email já cadastrado') || msgLower.includes('conta já existe')) {
          setMensagemCampoSenha('E-mail já cadastrado!');
          return;
        }
      }

      setMensagemCampoSenha('Não foi possível concluir o cadastro agora.');
    }
  };

  if (!autenticado) {
    return (
      <>
        {/* Menu Mobile */}
        <MobileMenuAdmin />

        <div className={`min-h-screen flex items-center justify-center bg-gray-900 text-white px-3 md:px-4 ${isMobile ? 'py-8' : 'py-4'}`}>
          <div className="bg-slate-800 p-6 md:p-8 rounded-xl shadow-xl w-full max-w-md">
            <div className="flex flex-col items-center mb-4">
              <img src={logo} alt="Logo Mifica" className="w-10 md:w-12 mb-2 md:mb-3" />
              <h2 className="text-xl md:text-2xl font-bold text-center">🔐 Acesso Restrito</h2>
              <p className="text-xs md:text-sm text-gray-300 text-center mt-2">Digite a senha para acessar o cadastro de administrador</p>
            </div>
  <form
    onSubmit={async (e) => {
      e.preventDefault();
      try {
        await api.post('/usuarios/validar-acesso-admin', { senhaAcesso });
        setAutenticado(true);
      } catch (err) {
        const msg = err?.response?.data || 'Senha incorreta';
        alert(typeof msg === 'string' ? msg : 'Senha incorreta');
      }
    }}
  >
    <input
      type="password"
      placeholder="Senha de acesso"
      value={senhaAcesso}
      onChange={e => setSenhaAcesso(e.target.value)}
      className="w-full px-3 md:px-4 py-2 mb-3 md:mb-4 text-sm md:text-base border border-gray-600 rounded-md bg-slate-700 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
    />
    <button
      type="submit"
      className="w-full bg-indigo-600 text-white py-2 text-sm md:text-base rounded-md font-semibold hover:bg-indigo-700 transition"
    >
      Entrar
    </button>
  </form>

          </div>
        </div>
      </>
    );
  }

  return (
    <>
      {/* Menu Mobile */}
      <MobileMenuAdmin />

      <div className={`min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 px-3 md:px-4 ${isMobile ? 'py-8' : 'py-4'}`}>
        <form
          onSubmit={handleCadastro}
          className="bg-white rounded-xl shadow-xl p-6 md:p-10 w-full max-w-lg border border-gray-300"
        >
          <div className="flex flex-col items-center mb-4 md:mb-6">
            <img src={logo} alt="Logo Mifica" className="w-10 md:w-12 mb-2 md:mb-3" />
            <h2 className="text-xl md:text-3xl font-bold text-gray-800 text-center">Cadastro de Administrador</h2>
            <p className="text-xs md:text-sm text-gray-500 text-center mt-2">Preencha os dados para criar uma conta administrativa</p>
          </div>

          <div className="space-y-3 md:space-y-4">
            <input
              type="text"
              placeholder="Nome completo"
              value={nome}
              onChange={e => setNome(e.target.value)}
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <input
              type="email"
              placeholder="Email institucional"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <input
              type="password"
              placeholder="Senha segura"
              value={senha}
              onChange={e => setSenha(e.target.value)}
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            {mensagemCampoSenha && (
              <p className="text-xs md:text-sm text-green-700 mt-1">{mensagemCampoSenha}</p>
            )}
            <input
              type="text"
              placeholder="Telefone"
              value={telefone}
              onChange={e => setTelefone(e.target.value)}
              required
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <div>
              <input
                type="text"
                placeholder="Data de nascimento (DD/MM/AAAA)"
                value={dataNascimento}
                onChange={handleDataChange}
                maxLength={10}
                required
                className={`w-full px-3 md:px-4 py-2 text-sm md:text-base border rounded-md focus:outline-none focus:ring-2 ${
                  erroData
                    ? 'border-red-500 focus:ring-red-500'
                    : 'border-gray-300 focus:ring-indigo-500'
                }`}
              />
              {erroData && (
                <p className="text-red-500 text-xs md:text-sm mt-1">{erroData}</p>
              )}
            </div>
            <select
              value={role}
              onChange={e => setRole(e.target.value)}
              className="w-full px-3 md:px-4 py-2 text-sm md:text-base border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="USER">Usuário</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <button
            type="submit"
            className="mt-4 md:mt-6 w-full bg-indigo-600 text-white py-2 text-sm md:text-base rounded-md font-semibold hover:bg-indigo-700 transition"
          >
            Cadastrar Administrador
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
