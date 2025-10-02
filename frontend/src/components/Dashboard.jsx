import React, { useEffect } from 'react';
import { authService } from '../services/auth';

const Dashboard = ({ onLogout }) => {
  const userInfo = authService.getUserInfo();

  useEffect(() => {
    document.title = 'PFT - Dashboard';
  }, []);

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#f0f9ff'
    }}>
      <div style={{
        backgroundColor: '#2563eb',
        padding: '1rem 0',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <h1 style={{
          textAlign: 'center',
          color: 'white',
          margin: 0,
          fontSize: '1.5rem'
        }}>
          Personal Finance Tracker
        </h1>
      </div>

      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: 'calc(100vh - 60px)',
        paddingTop: '2rem'
      }}>
        <div style={{
          background: 'white',
          padding: '3rem',
          borderRadius: '12px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
          width: '100%',
          maxWidth: '500px',
          textAlign: 'center'
        }}>
          <div style={{
            fontSize: '3rem',
            marginBottom: '1rem'
          }}>
            ✅
          </div>

          <h2 style={{
            color: '#059669',
            marginBottom: '1rem',
            fontSize: '2rem'
          }}>
            Login realizado com sucesso!
          </h2>

          <p style={{
            color: '#6b7280',
            marginBottom: '2rem',
            fontSize: '1.1rem'
          }}>
            Bem-vindo!
          </p>

        <div style={{
          backgroundColor: '#f3f4f6',
          padding: '1rem',
          borderRadius: '8px',
          marginBottom: '2rem'
        }}>
          <p style={{ margin: '0.5rem 0', fontWeight: 'bold' }}>
            📧 Email: {userInfo.email}
          </p>
          <p style={{ margin: '0.5rem 0', fontWeight: 'bold' }}>
            👤 Perfil: {userInfo.role}
          </p>
        </div>

        <button
          onClick={handleLogout}
          style={{
            padding: '0.75rem 2rem',
            backgroundColor: '#dc2626',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            fontSize: '1rem',
            cursor: 'pointer',
            fontWeight: 'bold'
          }}
        >
          Sair
        </button>
      </div>
      </div>
    </div>
  );
};

export default Dashboard;