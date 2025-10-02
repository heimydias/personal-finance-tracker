import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth';
import { usersService } from '../services/users';
import Header from './Header';

const UserProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const userInfo = authService.getUserInfo();

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    try {
      setLoading(true);
      setError('');

      const userId = userInfo.id;
      if (!userId) {
        setError('ID do usuário não encontrado. Faça login novamente.');
        return;
      }

      const userData = await usersService.getUserById(userId);
      setUser(userData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
        <Header />
        <div style={{ padding: '2rem', textAlign: 'center' }}>
          <p>Carregando perfil...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
        <Header />
        <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
          <div style={{
            padding: '1rem',
            backgroundColor: '#fee2e2',
            color: '#dc2626',
            borderRadius: '6px',
          }}>
            {error}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
        <div style={{
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          padding: '2rem',
        }}>
          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
            <div style={{
              width: '100px',
              height: '100px',
              borderRadius: '50%',
              backgroundColor: '#dbeafe',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '3rem',
              margin: '0 auto 1rem',
            }}>
              👤
            </div>
            <h2 style={{ margin: '0 0 0.5rem 0' }}>Meu Perfil</h2>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem', color: '#374151' }}>
              Nome
            </label>
            <div style={{
              padding: '0.75rem',
              backgroundColor: '#f3f4f6',
              borderRadius: '6px',
              color: '#1f2937',
              fontSize: '1.125rem',
              fontWeight: '500',
            }}>
              {user?.name}
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem', color: '#374151' }}>
              Email
            </label>
            <div style={{
              padding: '0.75rem',
              backgroundColor: '#f3f4f6',
              borderRadius: '6px',
              color: '#1f2937',
            }}>
              {user?.email}
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem', color: '#374151' }}>
              Perfil
            </label>
            <div style={{
              padding: '0.75rem',
              backgroundColor: '#f3f4f6',
              borderRadius: '6px',
            }}>
              <span style={{
                padding: '0.25rem 0.75rem',
                backgroundColor: user?.role === 'ADMIN' ? '#dbeafe' : '#e5e7eb',
                color: user?.role === 'ADMIN' ? '#1e40af' : '#374151',
                borderRadius: '12px',
                fontSize: '0.875rem',
                fontWeight: 'bold',
              }}>
                {user?.role}
              </span>
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem', color: '#374151' }}>
              Criado em
            </label>
            <div style={{
              padding: '0.75rem',
              backgroundColor: '#f3f4f6',
              borderRadius: '6px',
              color: '#1f2937',
            }}>
              {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('pt-BR', {
                day: '2-digit',
                month: 'long',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
              }) : '-'}
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem', color: '#374151' }}>
              Última atualização
            </label>
            <div style={{
              padding: '0.75rem',
              backgroundColor: '#f3f4f6',
              borderRadius: '6px',
              color: '#1f2937',
            }}>
              {user?.updatedAt ? new Date(user.updatedAt).toLocaleDateString('pt-BR', {
                day: '2-digit',
                month: 'long',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
              }) : '-'}
            </div>
          </div>

          <button
            onClick={() => user && navigate(`/users/${user.id}/edit`)}
            style={{
              width: '100%',
              padding: '0.75rem',
              backgroundColor: '#2563eb',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              cursor: 'pointer',
              fontWeight: 'bold',
            }}
          >
            Editar Perfil
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
