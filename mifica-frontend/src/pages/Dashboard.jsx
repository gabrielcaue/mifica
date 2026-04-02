import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';

// Componentes
import Topo from '../components/Topo';
import TransacoesList from '../components/TransacoesList';

export default function Dashboard() {
  const { usuario } = useAuth();
  const navigate = useNavigate();

  const [transacoes, setTransacoes] = useState([]);
  const [totalValor, setTotalValor] = useState(0);
  const [ultimaTransacao, setUltimaTransacao] = useState(null);

  useEffect(() => {
    api.get('/blockchain/transacoes')
      .then(response => {
        const lista = response.data;
        setTransacoes(lista);

        const total = lista.reduce((acc, tx) => acc + tx.valor, 0);
        setTotalValor(total);

        if (lista.length > 0) {
          setUltimaTransacao(lista[lista.length - 1]);
        }
      })
      .catch(error => console.error('Erro ao buscar transações:', error));
  }, []);

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
                onClick={() => navigate('/cadastro-admin')}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition mt-6"
              >
                Acessar Área Administrativa
              </button>
            )}
          </div>

          {/* Indicadores Blockchain */}
          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-indigo-500">
            <h2 className="text-xl md:text-2xl font-semibold text-indigo-300 mb-4">Indicadores Blockchain</h2>
            <p><strong>Total movimentado:</strong> R$ {totalValor.toFixed(2)}</p>
            {ultimaTransacao && (
              <p className="mt-2">
                <strong>Última transação:</strong> {ultimaTransacao.remetente} → {ultimaTransacao.destinatario} | R$ {ultimaTransacao.valor}
              </p>
            )}
          </div>
        </div>

        {/* Painel Administrativo */}
        {usuario.role === 'ROLE_ADMIN' && (
          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-red-500 mt-8 md:mt-10">
            <h2 className="text-xl md:text-2xl font-semibold text-red-400 mb-2">Painel Administrativo</h2>
            <p className="mb-4">Acesso rápido para ações administrativas:</p>
            <button
              onClick={() => navigate('/cadastro-admin')}
              className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition"
            >
              Gerenciar Usuários
            </button>
          </div>
        )}

        {/* Transações Blockchain */}
        <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-gray-500 mt-8 md:mt-10">
          <h2 className="text-xl md:text-2xl font-semibold text-gray-300 mb-4">Transações Blockchain</h2>

          <form className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <input
              type="text"
              placeholder="Hash"
              className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
              type="text"
              placeholder="Remetente"
              className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
              type="text"
              placeholder="Destinatário"
              className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
              type="number"
              placeholder="Valor"
              className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </form>

          <button
            type="submit"
            className="w-full md:w-auto px-6 py-2 border border-blue-500 text-blue-500 rounded-md font-semibold hover:bg-blue-500 hover:text-white transition"
          >
            Registrar
          </button>

          {/* Lista de transações */}
          <div className="mt-8">
            <TransacoesList />
          </div>
        </div>
      </div>
    </div>
  );
}
