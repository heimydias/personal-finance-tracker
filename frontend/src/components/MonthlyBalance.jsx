import React, { useState, useEffect } from 'react';
import { transactionsService } from '../services/transactions';
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
      const data = await transactionsService.getMonthlyBalance(year, month);
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

              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(3, 1fr)',
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
                    RECEITAS
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
                    DESPESAS
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: '#dc2626',
                  }}>
                    {formatCurrency(balance.totalExpense)}
                  </div>
                </div>

                <div style={{
                  backgroundColor: balance.balance >= 0 ? '#dbeafe' : '#fef3c7',
                  padding: '1.5rem',
                  borderRadius: '8px',
                  textAlign: 'center',
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
                    SALDO
                  </div>
                  <div style={{
                    fontSize: '1.25rem',
                    fontWeight: 'bold',
                    color: balance.balance >= 0 ? '#2563eb' : '#d97706',
                  }}>
                    {formatCurrency(balance.balance)}
                  </div>
                </div>
              </div>

              <div style={{
                backgroundColor: '#f3f4f6',
                padding: '1rem',
                borderRadius: '6px',
                textAlign: 'center',
              }}>
                <p style={{ margin: 0, color: '#6b7280', fontSize: '0.875rem' }}>
                  {balance.balance >= 0 ? (
                    <>✅ Saldo positivo! Você está no caminho certo.</>
                  ) : (
                    <>⚠️ Atenção: suas despesas superaram suas receitas este mês.</>
                  )}
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
