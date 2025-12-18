import { useAuth } from '../context/AuthContext';
import calcularNivel from '../utils/calcularNivel';
import { Link } from 'react-router-dom';
import Topo from '../components/Topo.jsx';

export default function Perfil() {
  const { usuario } = useAuth();

  if (!usuario) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
        <p className="text-xl">Você precisa estar logado para acessar o perfil.</p>
      </div>
    );
  }

  const nivel = calcularNivel(usuario.reputacao);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white">
      <Topo />

      <div className="max-w-4xl mx-auto px-6 py-10">
        <div className="bg-white/10 backdrop-blur-md rounded-xl p-6 shadow-lg border border-blue-500">
          <h2 className="text-3xl font-bold text-blue-300 mb-4">Perfil de {usuario.nome}</h2>
          <p><strong>Email:</strong> {usuario.email}</p>
          <p><strong>Reputação:</strong> {usuario.reputacao}</p>
          <p><strong>Nível:</strong> {nivel}</p>

          <div className="mt-6">
            <h3 className="text-xl font-semibold mb-2">Conquistas:</h3>
            <ul className="list-disc ml-6 text-sm">
              {usuario.conquistas?.length > 0 ? (
                usuario.conquistas.map((c, i) => <li key={i}>{c}</li>)
              ) : (
                <li>Nenhuma conquista registrada</li>
              )}
            </ul>
          </div>

          <Link
            to="/configuracoes"
            className="inline-block mt-6 px-6 py-2 border border-blue-500 text-blue-500 rounded-md font-semibold hover:bg-blue-500 hover:text-white transition"
          >
            Ir para Configurações
          </Link>
        </div>
      </div>
    </div>
  );
}
