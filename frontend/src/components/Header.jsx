import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/auth';
import Button from './common/Button';
import { colors } from '../theme/colors';

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const userInfo = authService.getUserInfo();
  const isAdmin = userInfo.role === 'ADMIN';

  const handleLogout = () => {
    authService.logout();
    window.location.href = '/login';
  };

  const isHomePage = location.pathname === '/';

  return (
    <div style={{
      backgroundColor: colors.primary,
      padding: '1rem 2rem',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      marginBottom: '2rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <h1
          onClick={() => navigate('/')}
          style={{
            color: 'white',
            margin: 0,
            fontSize: '1.5rem',
            cursor: 'pointer',
          }}
        >
          Personal Finance Tracker
        </h1>
        {!isHomePage && (
          <Button onClick={() => navigate('/')} variant="ghost" size="small" icon="🏠">
            Início
          </Button>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <Button onClick={() => navigate('/profile')} variant="ghost" size="small" icon="👤">
          Perfil
        </Button>
        {isAdmin && (
          <Button onClick={() => navigate('/users')} variant="ghost" size="small" icon="👥">
            Usuarios
          </Button>
        )}
        <Button onClick={handleLogout} variant="danger" size="small">
          Sair
        </Button>
      </div>
    </div>
  );
};

export default Header;
