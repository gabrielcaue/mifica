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

const calcularEntradaSaidaDoUsuario = (lista, emailUsuario) => {
  if (!emailUsuario) return { entrada: 0, saida: 0, total: 0 };

  const emailNormalizado = String(emailUsuario).toLowerCase();

  const entrada = lista.reduce((acc, tx) => {
    const destinatario = String(tx?.destinatario || '').toLowerCase();
    const valorTx = Number(tx?.valor || 0);
    return destinatario === emailNormalizado ? acc + valorTx : acc;
  }, 0);

  const saida = lista.reduce((acc, tx) => {
    const remetente = String(tx?.remetente || '').toLowerCase();
    const valorTx = Number(tx?.valor || 0);
    return remetente === emailNormalizado ? acc + valorTx : acc;
  }, 0);

  return {
    entrada,
    saida,
    total: entrada + saida
  };
};


export default function Dashboard() {
  const { usuario } = useAuth();
  const navigate = useNavigate();

  const [transacoes, setTransacoes] = useState([]);
  const [saldoDisponivel, setSaldoDisponivel] = useState(0);
  const [totalMovimentado, setTotalMovimentado] = useState(0);
  const [ultimaTransacao, setUltimaTransacao] = useState(null);
  const [destinatario, setDestinatario] = useState('');
  const [valor, setValor] = useState('');
  const [refreshTransacoes, setRefreshTransacoes] = useState(0);
  const [erroTransacao, setErroTransacao] = useState('');
  const [sucessoTransacao, setSucessoTransacao] = useState('');

  useEffect(() => {
    api.get('/blockchain/transacoes')
      .then(response => {
        const lista = response.data;
        setTransacoes(lista);

        const { entrada, saida } = calcularEntradaSaidaDoUsuario(lista, usuario?.email);
        const ehAdmin = usuario?.role === 'ROLE_ADMIN';

        // Valor total = saldo disponível
        if (ehAdmin) {
          // Admin: 1M - quanto já gastou
          setSaldoDisponivel(Math.max(0, 1_000_000 - saida));
        } else {
          // Comum: quanto recebeu - quanto gastou
          setSaldoDisponivel(Math.max(0, entrada - saida));
        }

        // Total movimentado = apenas quanto ele gastou (saida)
        setTotalMovimentado(saida);

        if (lista.length > 0) {
          setUltimaTransacao(lista[lista.length - 1]);
        }
      })
      .catch(error => console.error('Erro ao buscar transações:', error));
  }, [usuario?.email]);

  const handleRegistrarTransacao = async (e) => {
    e.preventDefault();
    setErroTransacao('');
    setSucessoTransacao('');

    if (!destinatario || !valor) {
      setErroTransacao('Informe destinatário e valor.');
      return;
    }

    try {
      await api.post('/transacoes', {
        destinatario,
        valor: Number(valor)
      });

      setSucessoTransacao('Transação registrada com sucesso!');
      setDestinatario('');
      setValor('');
      setRefreshTransacoes(prev => prev + 1);

      const response = await api.get('/blockchain/transacoes');
      const lista = response.data;
      setTransacoes(lista);
      
      const { entrada, saida } = calcularEntradaSaidaDoUsuario(lista, usuario?.email);
      const ehAdmin = usuario?.role === 'ROLE_ADMIN';

      if (ehAdmin) {
        setSaldoDisponivel(Math.max(0, 1_000_000 - saida));
      } else {
        setSaldoDisponivel(Math.max(0, entrada - saida));
      }

      setTotalMovimentado(saida);
      if (lista.length > 0) {
        setUltimaTransacao(lista[lista.length - 1]);
      }

      setTimeout(() => setSucessoTransacao(''), 3000);
    } catch (error) {
      console.error('Erro ao registrar transação:', error);
      const mensagemErro = error?.response?.data || 'Não foi possível registrar a transação.';
      if (typeof mensagemErro === 'string' && (mensagemErro.toLowerCase().includes('destinatário') || mensagemErro.toLowerCase().includes('não encontrado') || mensagemErro.toLowerCase().includes('nao encontrado'))) {
        setErroTransacao('Destinatário não encontrado.');
      } else {
        setErroTransacao(typeof mensagemErro === 'string' ? mensagemErro : 'Não foi possível registrar a transação.');
      }
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
            <p><strong>Valor total:</strong> {formatarReal(saldoDisponivel)}</p>
            <p><strong>Total movimentado:</strong> {formatarReal(totalMovimentado)}</p>
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

            <form onSubmit={handleRegistrarTransacao} className="space-y-4 mb-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
              </div>

              {erroTransacao && (
                <div className="p-3 md:p-4 bg-red-900/30 border border-red-500 rounded-md">
                  <p className="text-red-400 text-sm md:text-base font-semibold">⚠️ {erroTransacao}</p>
                </div>
              )}

              {sucessoTransacao && (
                <div className="p-3 md:p-4 bg-green-900/30 border border-green-500 rounded-md">
                  <p className="text-green-400 text-sm md:text-base font-semibold">✓ {sucessoTransacao}</p>
                </div>
              )}

              <div>
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
