import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

export default function AdminDashboard() {
  const [usuarios, setUsuarios] = useState([]);
  const [usuarioLogado, setUsuarioLogado] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');

    // 🔹 Buscar usuários
    axios.get('http://localhost:8080/api/admin/usuarios', {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(response => setUsuarios(response.data))
    .catch(error => console.error('Erro ao buscar usuários:', error));

    // 🔹 Buscar perfil do usuário logado
    axios.get('http://localhost:8080/api/usuarios/perfil', {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(response => setUsuarioLogado(response.data))
    .catch(error => console.error('Erro ao buscar perfil:', error));

    // 🔹 Verificar se está em modo dev
    axios.get('http://localhost:8080/api/config/dev-mode')
    .then(res => {
      if (res.data === true) {
        alert('⚠️ Você está em modo de desenvolvimento. Algumas permissões estão liberadas.');
      }
    });
  }, []);

  const promover = (id) => {
    const token = localStorage.getItem('token');
    axios.put(`http://localhost:8080/api/admin/promover/${id}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(() => alert('Usuário promovido!'))
    .catch(() => alert('Erro ao promover.'));
  };

  const rebaixar = (id) => {
    const token = localStorage.getItem('token');
    axios.put(`http://localhost:8080/api/admin/rebaixar/${id}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(() => alert('Usuário rebaixado!'))
    .catch(() => alert('Erro ao rebaixar.'));
  };

  return (
    <div className="container">
      <h1 className="title">Área Administrativa</h1>
      <p>Somente administradores podem acessar esta página.</p>

      <table style={{ width: '100%', marginTop: '20px', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Email</th>
            <th>Papel</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map(usuario => (
            <tr key={usuario.id}>
              <td>{usuario.nome}</td>
              <td>{usuario.email}</td>
              <td>{usuario.role}</td>
              <td>
                {usuarioLogado?.role === 'ROLE_ADMIN' && (
                  usuario.role === 'ROLE_USER' ? (
                    <button onClick={() => promover(usuario.id)}>Promover</button>
                  ) : (
                    <button onClick={() => rebaixar(usuario.id)}>Rebaixar</button>
                  )
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ marginTop: '30px' }}>
        <Link to="/cadastro">
          <button style={{
            padding: '10px 20px',
            backgroundColor: '#007bff',
            color: '#fff',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer'
          }}>
            Cadastrar Novo Usuário
          </button>
        </Link>
      </div>
    </div>
  );
}
