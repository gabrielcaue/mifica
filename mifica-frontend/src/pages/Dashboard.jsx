import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';

// Componentes
import Topo from '../components/Topo';
import TransacoesList from '../components/TransacoesList';

export default function Dashboard() {
  // ICP-TOTAL: 2
  // ICP-01: Dashboard consolida métricas financeiras por perfil (admin/comum) a partir do histórico blockchain.
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const LIMITE_ADMIN = 1_000_000;

  const [transacoes, setTransacoes] = useState([]);
  const [totalValor, setTotalValor] = useState(0);
  const [ultimaTransacao, setUltimaTransacao] = useState(null);
  const [saldoAdminDisponivel, setSaldoAdminDisponivel] = useState(LIMITE_ADMIN);
  const [saldoUsuariosComuns, setSaldoUsuariosComuns] = useState(0);
  const [saldoUsuarioComumLogado, setSaldoUsuarioComumLogado] = useState(0);
  const [destinatario, setDestinatario] = useState('');
  const [valor, setValor] = useState('');
  const [refreshTransacoes, setRefreshTransacoes] = useState(0);

  const calcularResumo = (lista, usuariosSistema = []) => {
    const emailLogado = (usuario?.email || '').toLowerCase();
    const ehAdmin = usuario?.role === 'ROLE_ADMIN';

    const saidaDoLogado = lista.reduce((acc, tx) => {
      const remetente = (tx.remetente || '').toLowerCase();
      return remetente === emailLogado ? acc + Number(tx.valor || 0) : acc;
    }, 0);

    const entradaDoLogado = lista.reduce((acc, tx) => {
      const destino = (tx.destinatario || '').toLowerCase();
      return destino === emailLogado ? acc + Number(tx.valor || 0) : acc;
    }, 0);

    const comuns = new Set(
      usuariosSistema
        .filter(u => u?.role === 'ROLE_USER')
        .map(u => (u?.email || '').toLowerCase())
        .filter(Boolean)
    );

    const saldoComuns = lista.reduce((acc, tx) => {
      const destino = (tx.destinatario || '').toLowerCase();
      const remetente = (tx.remetente || '').toLowerCase();
      const valorTx = Number(tx.valor || 0);

      if (comuns.has(destino)) acc += valorTx;
      if (comuns.has(remetente)) acc -= valorTx;

      return acc;
    }, 0);

    setTotalValor(saidaDoLogado);
    setSaldoAdminDisponivel(Math.max(0, LIMITE_ADMIN - saidaDoLogado));
    setSaldoUsuariosComuns(Math.max(0, saldoComuns));
    setSaldoUsuarioComumLogado(Math.max(0, entradaDoLogado - saidaDoLogado));
    setUltimaTransacao(lista.length > 0 ? lista[lista.length - 1] : null);

    if (!ehAdmin) {
      setTotalValor(entradaDoLogado + saidaDoLogado);
    }
  };

  const carregarTransacoes = async () => {
    try {
      const [resTransacoes, resUsuarios] = await Promise.all([
        api.get('/blockchain/transacoes'),
        usuario?.role === 'ROLE_ADMIN'
          ? api.get('/admin/usuarios')
          : Promise.resolve({ data: [] })
      ]);

      const lista = resTransacoes.data || [];
      setTransacoes(lista);
      calcularResumo(lista, resUsuarios.data || []);
    } catch (error) {
      console.error('Erro ao buscar transações:', error);
    }
  };

  useEffect(() => {
    if (!usuario) return;
    carregarTransacoes();
  }, [usuario?.email, usuario?.role]);

  const handleRegistrarTransacao = async (e) => {
    // ICP-02: Registro de transação força recarga imediata para refletir movimentação em tempo real.
    e.preventDefault();

    if (!destinatario || !valor) {
      alert('Informe destinatário e valor.');
      return;
    }

    try {
      await api.post('/blockchain/transacoes', {
        destinatario,
        valor: Number(valor)
      });

      setDestinatario('');
      setValor('');
      setRefreshTransacoes(prev => prev + 1);
      await carregarTransacoes();
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
            {usuario.role === 'ROLE_ADMIN' && (
              <p>
                <strong>Saldo do admin (padrão R$ 1.000.000):</strong> R$ {saldoAdminDisponivel.toFixed(2)}
              </p>
            )}
            <p><strong>Total movimentado:</strong> R$ {totalValor.toFixed(2)}</p>
            {usuario.role === 'ROLE_ADMIN' ? (
              <p className="mt-2">
                <strong>Saldo disponível usuários comuns:</strong> R$ {saldoUsuariosComuns.toFixed(2)}
              </p>
            ) : (
              <p className="mt-2">
                <strong>Seu saldo disponível:</strong> R$ {saldoUsuarioComumLogado.toFixed(2)}
              </p>
            )}
            {ultimaTransacao && (
              <p className="mt-2">
                <strong>Última transação:</strong> {ultimaTransacao.destinatario} | R$ {ultimaTransacao.valor}
              </p>
            )}
          </div>
        </div>

        {/* Transações Blockchain */}
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
      </div>
    </div>
  );
}
