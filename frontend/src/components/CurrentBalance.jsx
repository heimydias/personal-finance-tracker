import React, { useState, useEffect } from 'react';
import { balanceService } from '../services/balance';
import Header from './Header';
import Card from './common/Card';
import { formatCurrency } from '../utils/formatters';

const CurrentBalance = () => {
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadBalance();
  }, []);

  const loadBalance = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await balanceService.getCurrentBalance();
      setBalance(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '1000px', margin: '0 auto' }}>
        <h2 style={{ marginTop: 0, marginBottom: '2rem' }}>Saldo Consolidado</h2>

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

        {loading ? (
          <Card>
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
              Carregando saldo...
            </div>
          </Card>
        ) : balance ? (
          <div>
            {/* Cards principais */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '1rem',
              marginBottom: '2rem',
            }}>
              {/* Total de Receitas */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #d1fae5',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  📈
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#065f46',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  TOTAL RECEITAS
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#059669',
                  textAlign: 'center',
                }}>
                  {formatCurrency(balance.totalIncome)}
                </div>
              </div>

              {/* Total de Despesas */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #fee2e2',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  📉
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#991b1b',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  TOTAL DESPESAS
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#dc2626',
                  textAlign: 'center',
                }}>
                  {formatCurrency(balance.totalExpense)}
                </div>
              </div>

              {/* Saldo em Poupança */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #dbeafe',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  🏦
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#1e40af',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  SALDO EM POUPANÇA
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#2563eb',
                  textAlign: 'center',
                }}>
                  {formatCurrency(balance.savingsBalance)}
                </div>
              </div>
            </div>

            {/* Cards secundários */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
              gap: '1rem',
              marginBottom: '2rem',
            }}>
              {/* Saldo em Conta */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: balance.accountBalance >= 0 ? '2px solid #d1fae5' : '2px solid #fef3c7',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  {balance.accountBalance >= 0 ? '💰' : '⚠️'}
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: balance.accountBalance >= 0 ? '#065f46' : '#92400e',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  SALDO EM CONTA
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: balance.accountBalance >= 0 ? '#059669' : '#d97706',
                  textAlign: 'center',
                }}>
                  {formatCurrency(balance.accountBalance)}
                </div>
              </div>

              {/* Saldo Total */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                border: '3px solid #8b5cf6',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  💎
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#6b21a8',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  SALDO TOTAL
                </div>
                <div style={{
                  fontSize: '1.75rem',
                  fontWeight: 'bold',
                  color: '#8b5cf6',
                  textAlign: 'center',
                }}>
                  {formatCurrency(balance.totalBalance)}
                </div>
                <div style={{
                  fontSize: '0.75rem',
                  color: '#9ca3af',
                  textAlign: 'center',
                  marginTop: '0.5rem',
                }}>
                  (Conta + Poupança)
                </div>
              </div>
            </div>

            {/* Resumo / Observações */}
            <Card>
              <h3 style={{ marginTop: 0, color: '#374151' }}>Resumo</h3>
              <div style={{
                backgroundColor: '#f3f4f6',
                padding: '1rem',
                borderRadius: '6px',
                marginBottom: '1rem',
              }}>
                <p style={{ margin: '0 0 0.5rem 0', color: '#6b7280' }}>
                  <strong>Fórmula do saldo:</strong>
                </p>
                <p style={{ margin: 0, color: '#374151', fontFamily: 'monospace' }}>
                  Saldo Total = Receitas - Despesas = {formatCurrency(balance.totalIncome)} - {formatCurrency(balance.totalExpense)}
                </p>
                <p style={{ margin: '0.5rem 0 0 0', color: '#374151', fontFamily: 'monospace' }}>
                  Saldo em Conta = Saldo Total - Poupança = {formatCurrency(balance.totalBalance)} - {formatCurrency(balance.savingsBalance)}
                </p>
              </div>

              {balance.accountBalance < 0 && (
                <div style={{
                  backgroundColor: '#fef3c7',
                  padding: '1rem',
                  borderRadius: '6px',
                  border: '1px solid #fbbf24',
                }}>
                  <p style={{ margin: 0, color: '#92400e' }}>
                    ⚠️ <strong>Atenção:</strong> Seu saldo em conta está negativo. Isso significa que você tem mais dinheiro em poupanças do que o disponível total.
                    Considere resgatar parte das poupanças ou aumentar suas receitas.
                  </p>
                </div>
              )}

              {balance.accountBalance >= 0 && balance.savingsBalance > 0 && (
                <div style={{
                  backgroundColor: '#d1fae5',
                  padding: '1rem',
                  borderRadius: '6px',
                  border: '1px solid #10b981',
                }}>
                  <p style={{ margin: 0, color: '#065f46' }}>
                    ✅ <strong>Parabéns!</strong> Você tem R$ {formatCurrency(balance.accountBalance)} disponível em conta e
                    R$ {formatCurrency(balance.savingsBalance)} guardado em poupanças. Continue assim!
                  </p>
                </div>
              )}
            </Card>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default CurrentBalance;
