import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth';
import Header from './Header';

const Dashboard = () => {
  const navigate = useNavigate();
  const userInfo = authService.getUserInfo();

  useEffect(() => {
    document.title = 'PFT - Dashboard';
  }, []);

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#f0f9ff'
    }}>
      <Header />

      <div style={{
        maxWidth: '800px',
        margin: '0 auto',
        padding: '0 1rem',
      }}>
        <div style={{
          background: 'white',
          padding: '2rem',
          borderRadius: '12px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
          marginBottom: '2rem',
        }}>
          <h2 style={{
            color: '#059669',
            marginBottom: '2rem',
            fontSize: '2rem',
            textAlign: 'center',
          }}>
            Bem-vindo, {userInfo.name}!
          </h2>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(2, 1fr)',
            gap: '1rem',
          }}>
            <button
              onClick={() => navigate('/transactions')}
              style={{
                padding: '1.5rem',
                backgroundColor: '#8b5cf6',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1rem',
                cursor: 'pointer',
                fontWeight: 'bold',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
              }}
            >
              <span style={{ fontSize: '2rem' }}>💳</span>
              <span>Transações</span>
            </button>

            <button
              onClick={() => navigate('/savings')}
              style={{
                padding: '1.5rem',
                backgroundColor: '#10b981',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1rem',
                cursor: 'pointer',
                fontWeight: 'bold',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
              }}
            >
              <span style={{ fontSize: '2rem' }}>🏦</span>
              <span>Poupanças</span>
            </button>

            <button
              onClick={() => navigate('/balance')}
              style={{
                padding: '1.5rem',
                backgroundColor: '#8b5cf6',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1rem',
                cursor: 'pointer',
                fontWeight: 'bold',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
              }}
            >
              <span style={{ fontSize: '2rem' }}>💎</span>
              <span>Saldo Consolidado</span>
            </button>

            <button
              onClick={() => navigate('/balance/monthly')}
              style={{
                padding: '1.5rem',
                backgroundColor: '#f59e0b',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1rem',
                cursor: 'pointer',
                fontWeight: 'bold',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
              }}
            >
              <span style={{ fontSize: '2rem' }}>📊</span>
              <span>Saldo Mensal</span>
            </button>

            <button
              onClick={() => navigate('/forecast')}
              style={{
                padding: '1.5rem',
                backgroundColor: '#6366f1',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1rem',
                cursor: 'pointer',
                fontWeight: 'bold',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
                gridColumn: 'span 2',
              }}
            >
              <span style={{ fontSize: '2rem' }}>🔮</span>
              <span>Previsao Financeira</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
