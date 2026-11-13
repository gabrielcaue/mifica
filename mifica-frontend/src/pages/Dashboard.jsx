import { ethers } from 'ethers';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';

// Componentes
import Topo from '../components/Topo';
import TransacoesList from '../components/TransacoesList';

const formatarReal = (valor) => {
  if (valor === null || valor === undefined || isNaN(valor)) return 'R$ 0';

  const valorInteiro = Math.floor(valor);
  const valorFormatado = valorInteiro.toLocaleString('pt-BR');

  return `R$ ${valorFormatado}`;
};

const formatarEndereco = (endereco) => {
  if (!endereco) return '—';
  return `${endereco.slice(0, 6)}...${endereco.slice(-4)}`;
};

const formatarSaldoMatic = (saldo) => {
  if (!saldo) return '0.00';

  try {
    const valor = Number(ethers.formatEther(saldo));
    return valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 4,
    });
  } catch (error) {
    return '0.00';
  }
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
    total: entrada + saida,
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
  const [walletModalOpen, setWalletModalOpen] = useState(false);
  const [walletConnected, setWalletConnected] = useState(false);
  const [walletAddress, setWalletAddress] = useState('');
  const [walletBalance, setWalletBalance] = useState('0.00');
  const [walletNetwork, setWalletNetwork] = useState('Polygon');
  const [walletError, setWalletError] = useState('');
  const [isConnectingWallet, setIsConnectingWallet] = useState(false);

  useEffect(() => {
    api.get('/blockchain/transacoes')
      .then((response) => {
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
      })
      .catch((error) => console.error('Erro ao buscar transações:', error));
  }, [usuario?.email]);

  const syncWalletState = async () => {
    if (!window.ethereum) {
      setWalletConnected(false);
      setWalletAddress('');
      setWalletBalance('0.00');
      setWalletNetwork('MetaMask não disponível');
      return;
    }

    try {
      const provider = new ethers.BrowserProvider(window.ethereum);
      const accounts = await provider.listAccounts();

      if (!accounts.length) {
        setWalletConnected(false);
        setWalletAddress('');
        setWalletBalance('0.00');
        setWalletNetwork('Polygon');
        return;
      }

      const signer = await provider.getSigner();
      const address = await signer.getAddress();
      const network = await provider.getNetwork();
      const balance = await provider.getBalance(address);
      const chainId = Number(network.chainId);

      setWalletAddress(address);
      setWalletBalance(formatarSaldoMatic(balance));
      setWalletNetwork(chainId === 137 ? 'Polygon' : `Rede ${chainId}`);
      setWalletConnected(true);
      setWalletError('');
    } catch (error) {
      console.error('Erro ao sincronizar carteira:', error);
      setWalletConnected(false);
      setWalletAddress('');
      setWalletBalance('0.00');
      setWalletNetwork('Polygon');
    }
  };

  const connectWallet = async () => {
    setWalletError('');

    if (!window.ethereum) {
      setWalletError('MetaMask não encontrada. Instale a extensão para continuar.');
      return;
    }

    setIsConnectingWallet(true);

    try {
      const provider = new ethers.BrowserProvider(window.ethereum);
      const accounts = await provider.send('eth_requestAccounts', []);

      if (!accounts || !accounts.length) {
        throw new Error('Nenhuma conta foi selecionada na MetaMask.');
      }

      const [account] = accounts;
      const network = await provider.getNetwork();
      const balance = await provider.getBalance(account);
      const chainId = Number(network.chainId);

      setWalletAddress(account);
      setWalletBalance(formatarSaldoMatic(balance));
      setWalletNetwork(chainId === 137 ? 'Polygon' : `Rede ${chainId}`);
      setWalletConnected(true);
      setWalletModalOpen(false);

      if (chainId !== 137) {
        setWalletError('A carteira está em outra rede. Troque para Polygon para continuar.');
      }
    } catch (error) {
      console.error('Erro ao conectar carteira:', error);
      setWalletError(error?.message || 'Não foi possível conectar com a MetaMask.');
    } finally {
      setIsConnectingWallet(false);
    }
  };

  useEffect(() => {
    syncWalletState();

    if (!window.ethereum) return undefined;

    const handleAccountsChanged = () => syncWalletState();
    const handleChainChanged = () => syncWalletState();

    window.ethereum.on('accountsChanged', handleAccountsChanged);
    window.ethereum.on('chainChanged', handleChainChanged);

    return () => {
      window.ethereum.removeListener('accountsChanged', handleAccountsChanged);
      window.ethereum.removeListener('chainChanged', handleChainChanged);
    };
  }, []);

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
        valor: Number(valor),
      });

      setSucessoTransacao('Transação registrada com sucesso!');
      setDestinatario('');
      setValor('');
      setRefreshTransacoes((prev) => prev + 1);

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

      {walletModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4">
          <div className="w-full max-w-md rounded-2xl border border-blue-500/50 bg-slate-900 p-6 shadow-2xl">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-2xl font-semibold text-blue-300">Conectar carteira</h3>
              <button
                type="button"
                onClick={() => setWalletModalOpen(false)}
                className="text-gray-400 hover:text-white"
              >
                ✕
              </button>
            </div>

            <div className="space-y-4">
              <p className="text-sm text-gray-300">
                Conecte sua MetaMask para ver saldo, rede e status da carteira com segurança.
              </p>

              <div className="rounded-xl border border-dashed border-blue-500/60 bg-blue-500/5 p-4 text-sm text-blue-100">
                <p className="font-semibold mb-2">Informações importantes</p>
                <ul className="space-y-1 text-xs text-blue-100/90">
                  <li>• Conectar a carteira não cobra gás.</li>
                  <li>• Assinar ou enviar transação pode cobrar gás.</li>
                  <li>• A rede ideal para este app é Polygon.</li>
                </ul>
              </div>

              <button
                type="button"
                onClick={connectWallet}
                disabled={isConnectingWallet}
                className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isConnectingWallet ? 'Conectando...' : 'Conectar MetaMask'}
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="max-w-6xl mx-auto px-4 md:px-6 py-6 md:py-10">
        <h1 className="text-2xl md:text-4xl font-bold text-blue-400 mb-2">Bem-vindo ao Mifica</h1>
        <p className="text-sm md:text-lg text-gray-300 mb-6 md:mb-8">Construa sua reputação digital</p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
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

          <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 md:p-6 shadow-lg border border-indigo-500">
            <div className="flex items-center justify-between gap-3 mb-4">
              <h2 className="text-xl md:text-2xl font-semibold text-indigo-300">Carteira e saldo</h2>
              <span
                className={`rounded-full px-2 py-1 text-xs font-medium ${
                  walletConnected ? 'bg-green-500/20 text-green-300' : 'bg-gray-500/20 text-gray-300'
                }`}
              >
                {walletConnected ? 'Conectada' : 'Não conectada'}
              </span>
            </div>

            {!walletConnected ? (
              <div className="space-y-4">
                <div className="rounded-xl border border-dashed border-indigo-500/60 bg-indigo-500/5 p-4">
                  <p className="text-sm text-gray-300">Conecte sua carteira para visualizar o saldo, rede e movimentações em tempo real.</p>
                  <button
                    type="button"
                    onClick={() => setWalletModalOpen(true)}
                    className="mt-4 w-full rounded-lg bg-indigo-600 px-4 py-3 font-semibold text-white transition hover:bg-indigo-500"
                  >
                    Conectar MetaMask
                  </button>
                </div>

                <div className="grid grid-cols-1 gap-2 text-sm">
                  <p><strong>Valor total:</strong> {formatarReal(saldoDisponivel)}</p>
                  <p><strong>Total movimentado:</strong> {formatarReal(totalMovimentado)}</p>
                  <p><strong>Gas:</strong> gratuito para conectar. Cobrança apenas em transações na rede.</p>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="rounded-2xl border border-emerald-500/50 bg-emerald-500/5 p-4">
                  <div className="flex items-center justify-between text-xs text-emerald-200">
                    <span>Carteira</span>
                    <span>{walletNetwork}</span>
                  </div>
                  <p className="mt-2 text-xl font-semibold text-white">{formatarEndereco(walletAddress)}</p>
                  <p className="mt-3 text-3xl font-bold text-emerald-300">{walletBalance} MATIC</p>
                </div>

                <div className="grid grid-cols-1 gap-2 text-sm">
                  <p><strong>Valor total:</strong> {formatarReal(saldoDisponivel)}</p>
                  <p><strong>Total movimentado:</strong> {formatarReal(totalMovimentado)}</p>
                  <p><strong>Saldo da carteira:</strong> {walletBalance} MATIC</p>
                  <p><strong>Rede:</strong> {walletNetwork}</p>
                </div>

                <button
                  type="button"
                  onClick={() => setWalletModalOpen(true)}
                  className="w-full rounded-lg border border-emerald-500/60 bg-emerald-500/10 px-4 py-2 text-sm font-semibold text-emerald-200 hover:bg-emerald-500/20"
                >
                  Atualizar carteira
                </button>
              </div>
            )}

            {walletError && (
              <div className="mt-4 rounded-lg border border-amber-500/60 bg-amber-500/10 p-3 text-sm text-amber-200">
                {walletError}
              </div>
            )}

            {ultimaTransacao && (
              <p className="mt-4 text-sm text-gray-300">
                <strong>Última transação:</strong> {ultimaTransacao.destinatario} | {formatarReal(ultimaTransacao.valor)}
              </p>
            )}
          </div>
        </div>

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

            <div className="mt-8">
              <TransacoesList refreshKey={refreshTransacoes} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
