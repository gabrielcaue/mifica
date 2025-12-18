import { createContext, useContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [token, setToken] = useState(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    const tokenSalvo = localStorage.getItem('token');
    const usuarioSalvo = localStorage.getItem('usuario');

    if (tokenSalvo && usuarioSalvo) {
      try {
        const usuarioRecuperado = JSON.parse(usuarioSalvo);
        setUsuario(usuarioRecuperado);
        setToken(tokenSalvo);
      } catch (error) {
        console.error('Erro ao carregar usuário do localStorage:', error);
        localStorage.removeItem('usuario');
        localStorage.removeItem('token');
      }
    }

    setCarregando(false);
  }, []);

  const login = (dadosUsuario) => {
    const { token, id, _id, role, ...resto } = dadosUsuario;

    let roleFinal = role;
    try {
      const decoded = jwtDecode(token);
      // se o token tiver role, usa ele
      if (decoded?.role) {
        roleFinal = decoded.role;
      }
    } catch (error) {
      console.error('Erro ao decodificar o token:', error);
    }

    const usuarioFormatado = {
      ...resto,
      id: id || _id,   // garante que o ID seja salvo
      role: roleFinal  // mantém ROLE_ADMIN ou ROLE_USER
    };

    setUsuario(usuarioFormatado);
    setToken(token);

    localStorage.setItem('token', token);
    localStorage.setItem('usuario', JSON.stringify(usuarioFormatado));
  };

  const logout = () => {
    setUsuario(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
  };

  const value = {
    usuario,
    token,
    login,
    logout,
    // compara com ROLE_ADMIN para ficar consistente com backend
    isAdmin: usuario?.role === 'ROLE_ADMIN',
    carregando
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
