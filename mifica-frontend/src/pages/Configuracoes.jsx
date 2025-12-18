import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Topo from '../components/Topo';
import api from '../services/api';

export default function Configuracao() {
  const { usuario, token } = useAuth(); // token deve estar disponível no contexto
  const [senhaAtual, setSenhaAtual] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');
  const [mensagem, setMensagem] = useState('');

  const handleTrocarSenha = async (e) => {
    e.preventDefault();

    if (!usuario?.id) {
      setMensagem('ID do usuário não encontrado.');
      return;
    }

    if (novaSenha !== confirmarSenha) {
      setMensagem('As senhas não coincidem.');
      return;
    }

    try {
      await api.put(
        `/usuarios/${usuario.id}/senha`,
        {
          senhaAtual,
          senhaNova: novaSenha
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      setMensagem('Senha atualizada com sucesso!');
      setSenhaAtual('');
      setNovaSenha('');
      setConfirmarSenha('');
    } catch (error) {
      console.error('Erro ao atualizar senha:', error);
      setMensagem('Erro ao atualizar senha.');
    }
  };

  if (!usuario) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
        <p className="text-xl">Você precisa estar logado para acessar as configurações.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
      <Topo />

      <div className="max-w-4xl mx-auto px-6 py-10">
        <div className="bg-white/10 backdrop-blur-md rounded-xl p-6 shadow-lg border border-indigo-500">
          <h2 className="text-3xl font-bold text-indigo-300 mb-2">Configurações de Conta</h2>
          <p className="text-gray-300 mb-6">Ajuste suas preferências abaixo.</p>

          <div className="bg-white/10 rounded-lg p-4 border border-gray-300">
            <p><strong>Nome:</strong> {usuario.nome}</p>
            <p><strong>Email:</strong> {usuario.email}</p>

            <form onSubmit={handleTrocarSenha} className="mt-6 grid grid-cols-1 gap-4">
              <input
                type="password"
                placeholder="Senha atual"
                value={senhaAtual}
                onChange={(e) => setSenhaAtual(e.target.value)}
                className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <input
                type="password"
                placeholder="Nova senha"
                value={novaSenha}
                onChange={(e) => setNovaSenha(e.target.value)}
                className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <input
                type="password"
                placeholder="Confirmar nova senha"
                value={confirmarSenha}
                onChange={(e) => setConfirmarSenha(e.target.value)}
                className="bg-white text-gray-800 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />

              <button
                type="submit"
                className="px-6 py-2 border border-indigo-500 text-indigo-500 rounded-md font-semibold hover:bg-indigo-500 hover:text-white transition"
              >
                Salvar alterações
              </button>

              {mensagem && (
                <p className="text-sm mt-2 text-yellow-300">{mensagem}</p>
              )}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
