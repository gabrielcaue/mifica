import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../assets/logo.png';

export default function Topo() {
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white border-b border-gray-700">
      <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
        {/* Logo + Título */}
        <Link to="/dashboard" className="flex items-center gap-4 hover:opacity-80 transition">
          <img src={logo} alt="Logo Mifica" className="w-12 h-auto" />
          <div>
            <h2 className="text-xl font-bold text-blue-300">Mifica</h2>
            <p className="text-sm text-gray-300">Inteligência Modular para Software</p>
          </div>
        </Link>

        {/* Menu */}
        <div className="flex gap-4 items-center">
          <Link
            to="/dashboard"
            className="px-4 py-2 border border-gray-600 rounded text-white hover:bg-gray-700 transition"
          >
            Dashboard
          </Link>
          <Link
            to="/perfil"
            className="px-4 py-2 border border-gray-600 rounded text-white hover:bg-gray-700 transition"
          >
            Perfil
          </Link>
          <Link
            to="/configuracoes"
            className="px-4 py-2 border border-gray-600 rounded text-white hover:bg-gray-700 transition"
          >
            Configurações
          </Link>

          {/* Botão para admins */}
          {user?.role === 'admin' && (
            <a
              href="http://localhost:8501"
              target="_blank"
              rel="noopener noreferrer"
              className="px-4 py-2 border border-green-600 rounded text-green-300 hover:bg-green-700 transition"
            >
              🧠 Painel de Inteligência Mifica
            </a>
          )}

          <button
            onClick={handleLogout}
            className="px-4 py-2 border border-gray-600 rounded text-white hover:bg-red-600 transition"
          >
            Sair
          </button>
        </div>
      </div>
    </div>
  );
}
