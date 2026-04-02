import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Páginas
import Login from './pages/Login.jsx';
import Cadastro from './pages/Cadastro.jsx';
import CadastroAdmin from './pages/CadastroAdmin.jsx';
import Dashboard from './pages/Dashboard';
import Perfil from './pages/Perfil.jsx';
import Configuracao from './pages/Configuracoes.jsx';

// Componentes
import RotaAdmin from './components/RotaAdmin.jsx';

// Novo componente para embutir Streamlit
function AdminPanel() {
  const streamlitUrl = import.meta.env.VITE_STREAMLIT_URL
    || (import.meta.env.DEV ? 'http://localhost:8501' : `${window.location.origin}/streamlit`);

  return (
    <iframe
      src={streamlitUrl}
      style={{ width: "100%", height: "100vh", border: "none" }}
      title="Painel Administrativo"
    />
  );
}

function AdminPanelGate() {
  const acessoLiberado = sessionStorage.getItem('adminPanelAccess') === 'true';

  if (!acessoLiberado) {
    return <Navigate to="/cadastro-admin" replace />;
  }

  return <AdminPanel />;
}

function RotasProtegidas() {
  const { usuario } = useAuth();
  const location = useLocation();

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route path="/cadastro-admin" element={<CadastroAdmin />} />
      <Route
        path="/dashboard"
        element={
          usuario ? (
            <Dashboard />
          ) : (
            <Navigate to="/login" state={{ from: location.pathname }} />
          )
        }
      />
      <Route
        path="/admin"
        element={
          usuario ? (
            <RotaAdmin>
              <AdminPanelGate />
            </RotaAdmin>
          ) : (
            <Navigate to="/login" state={{ from: location.pathname }} />
          )
        }
      />
      <Route
        path="/perfil"
        element={
          usuario ? (
            <Perfil />
          ) : (
            <Navigate to="/login" state={{ from: location.pathname }} />
          )
        }
      />
      <Route
        path="/configuracoes"
        element={
          usuario ? (
            <Configuracao />
          ) : (
            <Navigate to="/login" state={{ from: location.pathname }} />
          )
        }
      />
    </Routes>
  );
}

export default function App() {
  const { carregando } = useAuth();

  if (carregando) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white text-xl">
        Carregando...
      </div>
    );
  }

  return <RotasProtegidas />;
}
