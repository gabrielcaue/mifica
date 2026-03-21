import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useMediaQuery from '../hooks/useMediaQuery';

/**
 * Menu Sidebar Mobile específico para a página de Cadastro Admin
 * Aparece como hamburger no lado direito em mobile
 */
export default function MobileMenuAdmin() {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const { isMobile } = useMediaQuery();

  if (!isMobile) return null;

  const menuItems = [
    { label: 'Cadastro Admin', icon: '🔑', path: '/cadastro-admin' },
    { label: 'Cadastro Comum', icon: '📝', path: '/cadastro' },
    { label: 'Login', icon: '🔓', path: '/login' },
  ];

  const handleMenuClick = (path) => {
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
        className="fixed top-4 right-4 z-50 p-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
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
        {/* Header */}
        <div className="p-6 border-b border-gray-700">
          <h2 className="text-lg font-bold text-green-300">Menu Admin</h2>
          <p className="text-sm text-gray-400 mt-2">Painel de administração</p>
        </div>

        {/* Menu Items */}
        <nav className="flex flex-col p-4 space-y-2">
          {menuItems.map((item) => (
            <button
              key={item.path}
              onClick={() => handleMenuClick(item.path)}
              className="w-full text-left px-4 py-3 rounded-lg text-gray-200 hover:bg-gray-700 hover:text-white transition flex items-center gap-3"
            >
              <span className="text-lg">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
      </div>
    </>
  );
}
