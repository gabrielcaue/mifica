import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import useMediaQuery from '../hooks/useMediaQuery';
import MobileNav from './MobileNav';
import logo from '../assets/logo.png';

export default function Topo() {
  const { logout, usuario } = useAuth();
  const navigate = useNavigate();
  const { isMobile } = useMediaQuery();

  const handleAdminAccess = () => {
    sessionStorage.setItem('adminPanelAccess', 'true');
    navigate('/admin');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white border-b border-gray-700">
      <div className="max-w-6xl mx-auto px-4 md:px-6 py-3 md:py-4 flex items-center justify-between">
        {/* Logo + Título */}
        <Link to="/dashboard" className="flex items-center gap-2 md:gap-4 hover:opacity-80 transition">
          <img src={logo} alt="Logo Mifica" className="w-10 md:w-12 h-auto" />
          {!isMobile && (
            <div>
              <h2 className="text-lg md:text-xl font-bold text-blue-300">Mifica</h2>
              <p className="text-xs md:text-sm text-gray-300">Inteligência Modular para Software</p>
            </div>
          )}
          {isMobile && (
            <h2 className="text-lg font-bold text-blue-300">Mifica</h2>
          )}
        </Link>

        {/* Menu Desktop */}
        {!isMobile && (
          <div className="flex gap-2 md:gap-4 items-center">
            <Link
              to="/dashboard"
              className="px-3 md:px-4 py-2 text-sm md:text-base border border-gray-600 rounded text-white hover:bg-gray-700 transition"
            >
              Dashboard
            </Link>
            <Link
              to="/perfil"
              className="px-3 md:px-4 py-2 text-sm md:text-base border border-gray-600 rounded text-white hover:bg-gray-700 transition"
            >
              Perfil
            </Link>
            <Link
              to="/configuracoes"
              className="px-3 md:px-4 py-2 text-sm md:text-base border border-gray-600 rounded text-white hover:bg-gray-700 transition"
            >
              Configurações
            </Link>

            {usuario?.role === 'ROLE_ADMIN' && (
              <button
                onClick={handleAdminAccess}
                className="px-3 md:px-4 py-2 text-sm md:text-base border border-green-600 rounded text-green-300 hover:bg-green-700 transition"
              >
                🧠 Área Admin
              </button>
            )}

            <button
              onClick={handleLogout}
              className="px-3 md:px-4 py-2 text-sm md:text-base border border-gray-600 rounded text-white hover:bg-red-600 transition"
            >
              Sair
            </button>
          </div>
        )}

        {isMobile && <MobileNav />}
      </div>
    </div>
  );
}
