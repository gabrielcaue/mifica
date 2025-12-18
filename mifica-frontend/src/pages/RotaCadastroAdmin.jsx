import { Navigate } from 'react-router-dom';

export default function RotaCadastroAdmin({ children }) {
  const acesso = localStorage.getItem('acessoAdmin');
  return acesso === 'true' ? children : <Navigate to="/acesso-admin" />;
}
