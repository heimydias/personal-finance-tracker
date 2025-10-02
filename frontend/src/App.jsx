import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import { authService } from './services/auth';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setIsAuthenticated(authService.isAuthenticated());
    setLoading(false);
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    setShowRegister(false);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setShowRegister(false);
  };

  const handleGoToRegister = () => {
    setShowRegister(true);
  };

  const handleBackToLogin = () => {
    setShowRegister(false);
  };

  const handleRegisterSuccess = () => {
    setShowRegister(false);
  };

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh'
      }}>
        Carregando...
      </div>
    );
  }

  return (
    <div className="App">
      {isAuthenticated ? (
        <Dashboard onLogout={handleLogout} />
      ) : showRegister ? (
        <Register
          onRegisterSuccess={handleRegisterSuccess}
          onBackToLogin={handleBackToLogin}
        />
      ) : (
        <Login
          onLoginSuccess={handleLoginSuccess}
          onGoToRegister={handleGoToRegister}
        />
      )}
    </div>
  );
}

export default App;
