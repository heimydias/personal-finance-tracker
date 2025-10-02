import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { usersService } from '../services/users';
import { authService } from '../services/auth';
import Header from './Header';

const UserForm = ({ isNew = false }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [loading, setLoading] = useState(false);
  const [loadingUser, setLoadingUser] = useState(!isNew);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const userInfo = authService.getUserInfo();
  const isAdmin = userInfo.role === 'ADMIN';

  useEffect(() => {
    if (!isNew && id) {
      loadUser();
    }
  }, [id, isNew]);

  const loadUser = async () => {
    try {
      setLoadingUser(true);
      setError('');
      const data = await usersService.getUserById(id);
      setName(data.name);
      setEmail(data.email);
      setRole(data.role);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoadingUser(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (isNew && password !== confirmPassword) {
      setError('As senhas não coincidem');
      return;
    }

    if (isNew && !password) {
      setError('A senha é obrigatória para novo usuário');
      return;
    }

    try {
      setLoading(true);
      if (isNew) {
        await usersService.createUser(name, email, password, role);
        setSuccess('Usuário criado com sucesso!');
        setTimeout(() => navigate('/users'), 1500);
      } else {
        await usersService.updateUser(id, name, email, password || null, role);
        setSuccess('Usuário atualizado com sucesso!');
        setTimeout(() => navigate('/users'), 1500);
      }
    } catch (err) {
      setError(err.fieldErrors || err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loadingUser) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center' }}>
        <p>Carregando usuário...</p>
      </div>
    );
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
      <div style={{
        backgroundColor: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        padding: '2rem',
      }}>
        <h2 style={{ marginTop: 0, marginBottom: '2rem' }}>
          {isNew ? 'Criar Novo Usuário' : 'Editar Usuário'}
        </h2>

        {error && (
          <div style={{
            padding: '1rem',
            backgroundColor: '#fee2e2',
            color: '#dc2626',
            borderRadius: '6px',
            marginBottom: '1rem',
          }}>
            {Array.isArray(error) ? (
              <ul style={{ margin: 0, paddingLeft: '20px' }}>
                {error.map((err, index) => (
                  <li key={index} style={{ marginBottom: '4px' }}>
                    {err}
                  </li>
                ))}
              </ul>
            ) : (
              error
            )}
          </div>
        )}

        {success && (
          <div style={{
            padding: '1rem',
            backgroundColor: '#d1fae5',
            color: '#065f46',
            borderRadius: '6px',
            marginBottom: '1rem',
          }}>
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Nome *
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
              placeholder="Digite o nome"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Email *
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
              placeholder="usuario@exemplo.com"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Senha {isNew ? '*' : '(deixe em branco para não alterar)'}
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required={isNew}
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
              placeholder="Digite a senha"
              minLength="6"
            />
          </div>

          {isNew && (
            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{
                display: 'block',
                fontWeight: 'bold',
                marginBottom: '0.5rem',
                color: '#374151',
              }}>
                Confirmar Senha *
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                style={{
                  width: '100%',
                  padding: '0.75rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '1rem',
                  boxSizing: 'border-box',
                }}
                placeholder="Confirme a senha"
                minLength="6"
              />
            </div>
          )}

          {isAdmin && (
            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{
                display: 'block',
                fontWeight: 'bold',
                marginBottom: '0.5rem',
                color: '#374151',
              }}>
                Perfil *
              </label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                required
                style={{
                  width: '100%',
                  padding: '0.75rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '1rem',
                  boxSizing: 'border-box',
                  backgroundColor: 'white',
                }}
              >
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
          )}

          <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: loading ? '#9ca3af' : '#2563eb',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontWeight: 'bold',
                fontSize: '1rem',
              }}
            >
              {loading ? 'Salvando...' : (isNew ? 'Criar Usuário' : 'Salvar Alterações')}
            </button>
            <button
              type="button"
              onClick={() => navigate(isAdmin ? '/users' : '/dashboard')}
              disabled={loading}
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: '#6b7280',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontWeight: 'bold',
                fontSize: '1rem',
              }}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UserForm;
