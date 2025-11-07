import React, { useState, useEffect } from 'react';
import { balanceService } from '../services/balance';
import Header from './Header';
import Card from './common/Card';
import { colors } from '../theme/colors';
import { formatCurrency, getMonthName } from '../utils/formatters';

const MonthlyBalance = () => {
  const currentDate = new Date();
  const [year, setYear] = useState(currentDate.getFullYear());
  const [month, setMonth] = useState(currentDate.getMonth() + 1);
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadBalance();
  }, [year, month]);

  const loadBalance = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await balanceService.getMonthlyBalance(year, month);
      setBalance(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };


  const generateYearOptions = () => {
    const years = [];
    const startYear = currentDate.getFullYear() - 5;
    const endYear = currentDate.getFullYear() + 1;
    for (let y = endYear; y >= startYear; y--) {
      years.push(y);
    }
    return years;
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
        <h2 style={{ marginTop: 0, marginBottom: '2rem' }}>Saldo Mensal</h2>

        <Card>
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ flex: 1 }}>
              <label style={{
                display: 'block',
                fontWeight: 'bold',
                marginBottom: '0.5rem',
                color: '#374151',
              }}>
                Mês
              </label>
              <select
                value={month}
                onChange={(e) => setMonth(parseInt(e.target.value))}
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
                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((m) => (
                  <option key={m} value={m}>
                    {getMonthName(m)}
                  </option>
                ))}
              </select>
            </div>

            <div style={{ flex: 1 }}>
              <label style={{
                display: 'block',
                fontWeight: 'bold',
                marginBottom: '0.5rem',
                color: '#374151',
              }}>
                Ano
              </label>
              <select
                value={year}
                onChange={(e) => setYear(parseInt(e.target.value))}
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
                {generateYearOptions().map((y) => (
                  <option key={y} value={y}>
                    {y}
                  </option>
                ))}
              </select>
            </div>
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

          {loading ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
              Carregando...
            </div>
          ) : balance ? (
            <div>
              <h3 style={{
                textAlign: 'center',
                color: '#374151',
                marginBottom: '2rem',
                fontSize: '1.5rem',
              }}>
                {getMonthName(balance.month)} de {balance.year}
              </h3>

              {/* Cards de Receitas e Despesas do Período */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '1rem',
                marginBottom: '2rem',
              }}>
                <div style={{
                  backgroundColor: '#d1fae5',
                  padding: '1.5rem',
                  borderRadius: '8px',
                  textAlign: 'center',
                }}>
                  <div style={{
                    fontSize: '2rem',
                    marginBottom: '0.5rem',
                  }}>
                    📈
                  </div>
                  <div style={{
                    fontSize: '0.875rem',
                    color: '#065f46',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}>
                    RECEITAS DO MÊS
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: '#059669',
                  }}>
                    {formatCurrency(balance.totalIncome)}
                  </div>
                </div>

                <div style={{
                  backgroundColor: '#fee2e2',
                  padding: '1.5rem',
                  borderRadius: '8px',
                  textAlign: 'center',
                }}>
                  <div style={{
                    fontSize: '2rem',
                    marginBottom: '0.5rem',
                  }}>
                    📉
                  </div>
                  <div style={{
                    fontSize: '0.875rem',
                    color: '#991b1b',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}>
                    DESPESAS DO MÊS
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: '#dc2626',
                  }}>
                    {formatCurrency(balance.totalExpense)}
                  </div>
                </div>
              </div>

              {/* Card de Saldo do Período */}
              <div style={{
                backgroundColor: balance.balance >= 0 ? '#dbeafe' : '#fef3c7',
                padding: '1.5rem',
                borderRadius: '8px',
                textAlign: 'center',
                marginBottom: '2rem',
                border: balance.balance >= 0 ? '2px solid #3b82f6' : '2px solid #f59e0b',
              }}>
                <div style={{
                  fontSize: '2rem',
                  marginBottom: '0.5rem',
                }}>
                  {balance.balance >= 0 ? '💰' : '⚠️'}
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: balance.balance >= 0 ? '#1e3a8a' : '#92400e',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                }}>
                  SALDO DO PERÍODO
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: balance.balance >= 0 ? '#2563eb' : '#d97706',
                }}>
                  {formatCurrency(balance.balance)}
                </div>
                <div style={{
                  fontSize: '0.75rem',
                  color: balance.balance >= 0 ? '#1e40af' : '#92400e',
                  marginTop: '0.5rem',
                }}>
                  (Receitas - Despesas - Poupança)
                </div>
              </div>

              {/* Cards de Saldos Acumulados */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '1rem',
                marginBottom: '2rem',
              }}>
                {/* Saldo em Conta */}
                <div style={{
                  backgroundColor: '#e0f2fe',
                  padding: '1.5rem',
                  borderRadius: '8px',
                  textAlign: 'center',
                }}>
                  <div style={{
                    fontSize: '2rem',
                    marginBottom: '0.5rem',
                  }}>
                    💳
                  </div>
                  <div style={{
                    fontSize: '0.875rem',
                    color: '#075985',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}>
                    SALDO EM CONTA
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: '#0284c7',
                  }}>
                    {formatCurrency(balance.accountBalance)}
                  </div>
                  <div style={{
                    fontSize: '0.75rem',
                    color: '#0369a1',
                    marginTop: '0.5rem',
                  }}>
                    Saldo acumulado até {getMonthName(balance.month)}
                  </div>
                </div>

                {/* Saldo em Poupança */}
                <div style={{
                  backgroundColor: balance.monthlySavingsBalance > 0 ? '#d1fae5' : '#f3f4f6',
                  padding: '1.5rem',
                  borderRadius: '8px',
                  textAlign: 'center',
                }}>
                  <div style={{
                    fontSize: '2rem',
                    marginBottom: '0.5rem',
                  }}>
                    🏦
                  </div>
                  <div style={{
                    fontSize: '0.875rem',
                    color: balance.monthlySavingsBalance > 0 ? '#065f46' : '#6b7280',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}>
                    SALDO EM POUPANÇA
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: balance.monthlySavingsBalance > 0 ? '#059669' : '#6b7280',
                  }}>
                    {formatCurrency(balance.monthlySavingsBalance)}
                  </div>
                  <div style={{
                    fontSize: '0.75rem',
                    color: balance.monthlySavingsBalance > 0 ? '#047857' : '#9ca3af',
                    marginTop: '0.5rem',
                  }}>
                    Saldo em poupança do mês
                  </div>
                </div>
              </div>

              {/* Resumo */}
              <div style={{
                backgroundColor: '#f3f4f6',
                padding: '1rem',
                borderRadius: '6px',
              }}>
                <p style={{ margin: '0 0 0.5rem 0', color: '#6b7280', fontSize: '0.875rem' }}>
                  {balance.balance >= 0 ? (
                    <>✅ <strong>Saldo do período positivo!</strong> Suas receitas superaram as despesas em {getMonthName(balance.month)}.</>
                  ) : (
                    <>⚠️ <strong>Atenção:</strong> Suas despesas superaram suas receitas em {getMonthName(balance.month)}.</>
                  )}
                </p>
                <p style={{ margin: '0.5rem 0 0 0', color: '#6b7280', fontSize: '0.75rem' }}>
                  Os saldos em conta e poupança mostram sua situação atual consolidada (não apenas deste mês).
                </p>
              </div>
            </div>
          ) : null}
        </Card>
      </div>
    </div>
  );
};

export default MonthlyBalance;
