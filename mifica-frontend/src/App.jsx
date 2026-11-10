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
  const apiBaseUrl = (import.meta.env.VITE_API_URL || 'https://mifica-production.up.railway.app').replace(/\/api\/?$/, '');
  const token = localStorage.getItem('token') || sessionStorage.getItem('token') || '';

  const defaultStreamlitUrl = 'https://streamlit-production-12a3.up.railway.app/streamlit';
  const configuredStreamlitUrl = (import.meta.env.VITE_STREAMLIT_URL || '').trim();

  const safeParse = (value) => {
    try {
      return new URL(value);
    } catch {
      return null;
    }
  };

  const apiUrlParsed = safeParse(apiBaseUrl);
  const configuredStreamlitParsed = safeParse(configuredStreamlitUrl);

  // Proteção: se VITE_STREAMLIT_URL estiver apontando para o backend, usa o domínio do Streamlit.
  const isUsingBackendAsStreamlit =
    !!(apiUrlParsed && configuredStreamlitParsed && apiUrlParsed.origin === configuredStreamlitParsed.origin);

  const streamlitBaseUrl = (
    configuredStreamlitUrl && !isUsingBackendAsStreamlit
      ? configuredStreamlitUrl
      : defaultStreamlitUrl
  );

  const normalizeStreamlitUrl = (rawUrl) => {
    let url = (rawUrl || '').trim();

    if (!/^https?:\/\//i.test(url)) {
      url = `https://${url}`;
    }

    // GitHub Pages roda em HTTPS: nunca permitir HTTP no iframe
    url = url.replace(/^http:\/\//i, 'https://');

    const parsed = safeParse(url);
    if (!parsed) {
      return `${defaultStreamlitUrl}/`;
    }

    // Evita redirect de /streamlit para /streamlit/ (que pode sair com http atrás de proxy)
    parsed.pathname = `${parsed.pathname.replace(/\/+$/, '')}/`;
    parsed.search = '';
    parsed.hash = '';

    return parsed.toString();
  };

  let streamlitUrl = normalizeStreamlitUrl(streamlitBaseUrl);

  if (token) {
    const urlComToken = safeParse(streamlitUrl);
    if (urlComToken) {
      urlComToken.searchParams.set('token', token);
      streamlitUrl = urlComToken.toString();
    }
  }

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
