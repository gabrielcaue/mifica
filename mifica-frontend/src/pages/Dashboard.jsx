import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';

// Componentes
import Topo from '../components/Topo';
import TransacoesList from '../components/TransacoesList';
// Função para formatar valores em Real com as regras especificadas
const formatarReal = (valor) => {
  if (valor === null || valor === undefined || isNaN(valor)) return 'R$ 0';
  
  // Converter para inteiro (ignorar centavos)
  const valorInteiro = Math.floor(valor);
  
  // Formatar com ponto a partir de 1000
  const valorFormatado = valorInteiro.toLocaleString('pt-BR');
  
  return `R$ ${valorFormatado}`;
};

const calcularTotalMovimentadoDoUsuario = (lista, emailUsuario) => {
  if (!emailUsuario) return 0;

  const emailNormalizado = String(emailUsuario).toLowerCase();

  return lista.reduce((acc, tx) => {
    const remetente = String(tx?.remetente || '').toLowerCase();
    const destinatario = String(tx?.destinatario || '').toLowerCase();
    const valorTx = Number(tx?.valor || 0);

    if (remetente === emailNormalizado || destinatario === emailNormalizado) {
      return acc + valorTx;
    }

    return acc;
  }, 0);
};


export default function Dashboard() {
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const valorTotalPerfil = usuario?.role === 'ROLE_ADMIN' ? 1_000_000 : 0;

  const [transacoes, setTransacoes] = useState([]);
  const [totalValor, setTotalValor] = useState(0);
  const [ultimaTransacao, setUltimaTransacao] = useState(null);
  const [destinatario, setDestinatario] = useState('');
  const [valor, setValor] = useState('');
  const [refreshTransacoes, setRefreshTransacoes] = useState(0);

  useEffect(() => {
    api.get('/blockchain/transacoes')
      .then(response => {
        const lista = response.data;
        setTransacoes(lista);

        const total = calcularTotalMovimentadoDoUsuario(lista, usuario?.email);
        setTotalValor(total);

        if (lista.length > 0) {
          setUltimaTransacao(lista[lista.length - 1]);
        }
      })
      .catch(error => console.error('Erro ao buscar transações:', error));
  }, [usuario?.email]);

  const handleRegistrarTransacao = async (e) => {
    e.preventDefault();

    if (!destinatario || !valor) {
      alert('Informe destinatário e valor.');
      return;
    }

    try {
      await api.post('/transacoes', {
        destinatario,
        valor: Number(valor)
      });

      setDestinatario('');
      setValor('');
      setRefreshTransacoes(prev => prev + 1);

      const response = await api.get('/blockchain/transacoes');
      const lista = response.data;
      setTransacoes(lista);
      const total = calcularTotalMovimentadoDoUsuario(lista, usuario?.email);
      setTotalValor(total);
      if (lista.length > 0) {
        setUltimaTransacao(lista[lista.length - 1]);
      }
    } catch (error) {
      console.error('Erro ao registrar transação:', error);
      alert('Não foi possível registrar a transação.');
    }
  };

  if (!usuario) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
        <p className="text-xl">Você precisa estar logado para acessar o dashboard.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
      <Topo />

      <div className="max-w-6xl mx-auto px-4 md:px-6 py-6 md:py-10">
        {/* Boas-vindas */}
        <h1 className="text-2xl md:text-4xl font-bold text-blue-400 mb-2">Bem-vindo ao Mifica</h1>
        <p className="text-sm md:text-lg text-gray-300 mb-6 md:mb-8">Construa sua reputação digital</p>

        {/* Perfil + Indicadores */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Perfil do Usuário */}
          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-blue-500">
            <h2 className="text-xl md:text-2xl font-semibold text-blue-300 mb-4">Perfil do Usuário</h2>
            <p><strong>Nome:</strong> {usuario.nome}</p>
            <p><strong>Email:</strong> {usuario.email}</p>
            <p><strong>Reputação:</strong> {usuario.reputacao}</p>

            <div className="mt-4">
              <p className="font-semibold mb-2">Conquistas:</p>
              <ul className="list-disc ml-6 text-xs md:text-sm">
                {usuario.conquistas?.length > 0 ? (
                  usuario.conquistas.map((c, i) => <li key={i}>{c}</li>)
                ) : (
                  <li>Nenhuma conquista registrada</li>
                )}
              </ul>
            </div>

            {/* Botão exclusivo para admins */}
            {usuario.role === 'ROLE_ADMIN' && (
              <button
                onClick={() => {
                  sessionStorage.setItem('adminPanelAccess', 'true');
                  navigate('/admin');
                }}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition mt-6"
              >
                Acessar Área Administrativa
              </button>
            )}
          </div>

          {/* Indicadores Blockchain */}
          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-indigo-500">
            <h2 className="text-xl md:text-2xl font-semibold text-indigo-300 mb-4">Indicadores Blockchain</h2>
            <p><strong>Valor total:</strong> {formatarReal(valorTotalPerfil)}</p>
            <p><strong>Total movimentado:</strong> {formatarReal(totalValor)}</p>
            {ultimaTransacao && (
              <p className="mt-2">
                <strong>Última transação:</strong> {ultimaTransacao.destinatario} | {formatarReal(ultimaTransacao.valor)}
              </p>
            )}
          </div>
        </div>

        {/* Transações Blockchain - apenas admin */}
        {usuario.role === 'ROLE_ADMIN' && (
          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-gray-500 mt-8 md:mt-10">
            <h2 className="text-xl md:text-2xl font-semibold text-gray-300 mb-4">Transações Blockchain</h2>

            <form onSubmit={handleRegistrarTransacao} className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
              <input
                type="text"
                placeholder="Destinatário"
                value={destinatario}
                onChange={(e) => setDestinatario(e.target.value)}
                className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <input
                type="number"
                placeholder="Valor"
                value={valor}
                onChange={(e) => setValor(e.target.value)}
                className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <div className="md:col-span-2">
                <button
                  type="submit"
                  className="w-full md:w-auto px-6 py-2 border border-blue-500 text-blue-500 rounded-md font-semibold hover:bg-blue-500 hover:text-white transition"
                >
                  Registrar
                </button>
              </div>
            </form>

            {/* Lista de transações */}
            <div className="mt-8">
              <TransacoesList refreshKey={refreshTransacoes} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
