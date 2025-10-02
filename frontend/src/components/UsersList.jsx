import React, { useState, useEffect } from 'react';
import { usersService } from '../services/users';
import { useNavigate } from 'react-router-dom';
import Header from './Header';

const UsersList = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);

  useEffect(() => {
    loadUsers();
  }, [page]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await usersService.listUsers(page, 10);
      setUsers(response.content);
      setTotalPages(Math.ceil(response.elements / response.pageSize));
      setTotalElements(response.elements);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!userToDelete) return;

    try {
      await usersService.deleteUser(userToDelete.id);
      setShowDeleteModal(false);
      setUserToDelete(null);
      loadUsers();
    } catch (err) {
      alert(err.message);
      setShowDeleteModal(false);
      setUserToDelete(null);
    }
  };

  const confirmDelete = (user) => {
    setUserToDelete(user);
    setShowDeleteModal(true);
  };

  if (loading) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center' }}>
        <p>Carregando usuários...</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h2 style={{ margin: 0 }}>Gerenciar Usuários</h2>
        <button
          onClick={() => navigate('/users/new')}
          style={{
            padding: '0.75rem 1.5rem',
            backgroundColor: '#2563eb',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            cursor: 'pointer',
            fontWeight: 'bold',
          }}
        >
          + Novo Usuário
        </button>
      </div>

      {error && (
        <div style={{
          padding: '1rem',
          backgroundColor: '#fee2e2',
          color: '#dc2626',
          borderRadius: '6px',
          marginBottom: '1rem',
        }}>
          {error}
        </div>
      )}

      <div style={{
        backgroundColor: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        overflow: 'hidden',
      }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Nome</th>
              <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Email</th>
              <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Perfil</th>
              <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Criado em</th>
              <th style={{ padding: '1rem', textAlign: 'center', fontWeight: 'bold' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                <td style={{ padding: '1rem', fontWeight: '500' }}>{user.name}</td>
                <td style={{ padding: '1rem', color: '#6b7280' }}>{user.email}</td>
                <td style={{ padding: '1rem' }}>
                  <span style={{
                    padding: '0.25rem 0.75rem',
                    backgroundColor: user.role === 'ADMIN' ? '#dbeafe' : '#e5e7eb',
                    color: user.role === 'ADMIN' ? '#1e40af' : '#374151',
                    borderRadius: '12px',
                    fontSize: '0.875rem',
                    fontWeight: 'bold',
                  }}>
                    {user.role}
                  </span>
                </td>
                <td style={{ padding: '1rem', color: '#6b7280' }}>
                  {new Date(user.createdAt).toLocaleDateString('pt-BR')}
                </td>
                <td style={{ padding: '1rem', textAlign: 'center' }}>
                  <button
                    onClick={() => navigate(`/users/${user.id}`)}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: '#3b82f6',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      marginRight: '0.5rem',
                    }}
                  >
                    👁 Ver
                  </button>
                  <button
                    onClick={() => navigate(`/users/${user.id}/edit`)}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: '#10b981',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      marginRight: '0.5rem',
                    }}
                  >
                    ✏️ Editar
                  </button>
                  <button
                    onClick={() => confirmDelete(user)}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: '#ef4444',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                    }}
                  >
                    🗑 Deletar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {users.length === 0 && (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#6b7280' }}>
            Nenhum usuário encontrado
          </div>
        )}
      </div>

      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '1rem' }}>
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: page === 0 ? '#e5e7eb' : '#2563eb',
              color: page === 0 ? '#9ca3af' : 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: page === 0 ? 'not-allowed' : 'pointer',
            }}
          >
            Anterior
          </button>
          <span style={{ padding: '0.5rem 1rem', display: 'flex', alignItems: 'center' }}>
            Página {page + 1} de {totalPages} ({totalElements} usuários)
          </span>
          <button
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            disabled={page >= totalPages - 1}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: page >= totalPages - 1 ? '#e5e7eb' : '#2563eb',
              color: page >= totalPages - 1 ? '#9ca3af' : 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer',
            }}
          >
            Próxima
          </button>
        </div>
      )}

      {showDeleteModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000,
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '2rem',
            borderRadius: '8px',
            maxWidth: '400px',
            width: '90%',
          }}>
            <h3 style={{ marginTop: 0 }}>Confirmar Deleção</h3>
            <p>Tem certeza que deseja deletar o usuário <strong>{userToDelete?.name}</strong> ({userToDelete?.email})?</p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
              <button
                onClick={() => {
                  setShowDeleteModal(false);
                  setUserToDelete(null);
                }}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Cancelar
              </button>
              <button
                onClick={handleDelete}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: '#ef4444',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Deletar
              </button>
            </div>
          </div>
        </div>
      )}
      </div>
    </div>
  );
};

export default UsersList;
