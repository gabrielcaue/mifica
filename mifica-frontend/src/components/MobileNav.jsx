import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import useMediaQuery from '../hooks/useMediaQuery';
import logo from '../assets/logo.png';

/**
 * Componente de Navegação Mobile
 * Aparece como hamburger menu em dispositivos móveis
 * Se for admin, pode incluir links para painel admin
 */
export default function MobileNav({ additionalMenuItems = [] }) {
  const [isOpen, setIsOpen] = useState(false);
  const { logout, usuario } = useAuth();
  const navigate = useNavigate();
  const { isMobile } = useMediaQuery();

  if (!isMobile) return null; // Só renderiza em mobile

  const handleLogout = () => {
    logout();
    setIsOpen(false);
    navigate('/');
  };

  const handleNavClick = (path) => {
    if (path === '/admin') {
      sessionStorage.setItem('adminPanelAccess', 'true');
    }
    navigate(path);
    setIsOpen(false);
  };

  return (
    <>
      {/* Overlay */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Hamburger Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="fixed top-4 right-4 z-50 p-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          {isOpen ? (
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          ) : (
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
          )}
        </svg>
      </button>

      {/* Sidebar Menu */}
      <div
        className={`fixed top-0 right-0 h-screen w-64 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900 text-white transform transition-transform duration-300 ease-in-out z-40 ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
      >
        {/* Header da Sidebar */}
        <div className="p-6 border-b border-gray-700">
          <div className="flex items-center gap-2 mb-2">
            <img src={logo} alt="Logo Mifica" className="w-8 h-auto" />
            <h2 className="text-lg font-bold text-blue-300">Mifica</h2>
          </div>
          {usuario && <p className="text-sm text-gray-400">Bem-vindo, {usuario.nome || 'Usuário'}</p>}
        </div>

        {/* Menu Items */}
        <nav className="flex flex-col p-4 space-y-2">
          {/* Itens adicionais (passados como prop) */}
          {additionalMenuItems.map((item, idx) => (
            <button
              key={idx}
              onClick={() => handleNavClick(item.path)}
              className="w-full text-left px-4 py-3 rounded-lg text-gray-200 hover:bg-gray-700 hover:text-white transition flex items-center gap-3"
            >
              {item.icon && <span className="text-lg">{item.icon}</span>}
              <span>{item.label}</span>
            </button>
          ))}

          {/* Itens padrão para usuários autenticados */}
          {usuario && (
            <>
              <button
                onClick={() => handleNavClick('/dashboard')}
                className="w-full text-left px-4 py-3 rounded-lg text-gray-200 hover:bg-gray-700 hover:text-white transition flex items-center gap-3"
              >
                <span>📊</span>
                <span>Dashboard</span>
              </button>
              <button
                onClick={() => handleNavClick('/perfil')}
                className="w-full text-left px-4 py-3 rounded-lg text-gray-200 hover:bg-gray-700 hover:text-white transition flex items-center gap-3"
              >
                <span>👤</span>
                <span>Perfil</span>
              </button>
              <button
                onClick={() => handleNavClick('/configuracoes')}
                className="w-full text-left px-4 py-3 rounded-lg text-gray-200 hover:bg-gray-700 hover:text-white transition flex items-center gap-3"
              >
                <span>⚙️</span>
                <span>Configurações</span>
              </button>

              {usuario?.role === 'ROLE_ADMIN' && (
                <button
                  onClick={() => handleNavClick('/admin')}
                  className="w-full text-left px-4 py-3 rounded-lg text-green-300 hover:bg-green-700 transition flex items-center gap-3"
                >
                  <span>🧠</span>
                  <span>Área Admin</span>
                </button>
              )}

              {/* Logout */}
              <button
                onClick={handleLogout}
                className="w-full text-left px-4 py-3 rounded-lg text-red-300 hover:bg-red-700 transition flex items-center gap-3"
              >
                <span>🚪</span>
                <span>Sair</span>
              </button>
            </>
          )}
        </nav>
      </div>
    </>
  );
}
