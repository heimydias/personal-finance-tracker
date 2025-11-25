import React, { useState, useEffect } from 'react';
import { forecastService } from '../services/forecast';
import Header from './Header';
import Card from './common/Card';
import { formatCurrency, getMonthName } from '../utils/formatters';

const Forecast = () => {
  const [forecast, setForecast] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [months, setMonths] = useState(3);

  useEffect(() => {
    loadForecast();
  }, [months]);

  const loadForecast = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await forecastService.getForecast(months);
      setForecast(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getBalanceVariation = () => {
    if (!forecast) return { value: 0, isPositive: true };
    const variation = forecast.projectedTotalBalance - forecast.currentTotalBalance;
    return {
      value: variation,
      isPositive: variation >= 0,
    };
  };

  const variation = getBalanceVariation();

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '2rem',
          flexWrap: 'wrap',
          gap: '1rem',
        }}>
          <h2 style={{ margin: 0 }}>Previsao Financeira</h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <label htmlFor="months" style={{ fontWeight: 'bold', color: '#374151' }}>
              Periodo de analise:
            </label>
            <select
              id="months"
              value={months}
              onChange={(e) => setMonths(Number(e.target.value))}
              style={{
                padding: '0.5rem 1rem',
                borderRadius: '6px',
                border: '1px solid #d1d5db',
                fontSize: '1rem',
                cursor: 'pointer',
              }}
            >
              <option value={1}>1 mes</option>
              <option value={3}>3 meses</option>
              <option value={6}>6 meses</option>
              <option value={12}>12 meses</option>
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
          <Card>
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
              Calculando previsao...
            </div>
          </Card>
        ) : forecast ? (
          <div>
            {/* Destaque: Previsao para o proximo mes */}
            <div style={{
              backgroundColor: '#8b5cf6',
              color: 'white',
              padding: '1.5rem',
              borderRadius: '12px',
              marginBottom: '2rem',
              textAlign: 'center',
            }}>
              <div style={{ fontSize: '1rem', marginBottom: '0.5rem', opacity: 0.9 }}>
                Previsao para
              </div>
              <div style={{ fontSize: '1.75rem', fontWeight: 'bold' }}>
                {getMonthName(forecast.targetMonth)} de {forecast.targetYear}
              </div>
              <div style={{ fontSize: '0.875rem', marginTop: '0.5rem', opacity: 0.8 }}>
                Baseado nos ultimos {forecast.monthsAnalyzed} {forecast.monthsAnalyzed === 1 ? 'mes' : 'meses'} analisados
              </div>
            </div>

            {/* Cards de Medias */}
            <h3 style={{ marginBottom: '1rem', color: '#374151' }}>Medias Mensais</h3>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '1rem',
              marginBottom: '2rem',
            }}>
              {/* Media de Receitas */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                borderLeft: '4px solid #059669',
              }}>
                <div style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.5rem' }}>
                  Media de Receitas
                </div>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#059669' }}>
                  {formatCurrency(forecast.averageIncome)}
                </div>
              </div>

              {/* Media de Despesas */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                borderLeft: '4px solid #dc2626',
              }}>
                <div style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.5rem' }}>
                  Media de Despesas
                </div>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#dc2626' }}>
                  {formatCurrency(forecast.averageExpense)}
                </div>
              </div>

              {/* Media de Depositos em Poupanca */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                borderLeft: '4px solid #2563eb',
              }}>
                <div style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.5rem' }}>
                  Media em Poupanca
                </div>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#2563eb' }}>
                  {formatCurrency(forecast.averageSavingsDeposit)}
                </div>
              </div>
            </div>

            {/* Cards de Projecao */}
            <h3 style={{ marginBottom: '1rem', color: '#374151' }}>Projecao para {getMonthName(forecast.targetMonth)}</h3>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
              gap: '1rem',
              marginBottom: '2rem',
            }}>
              {/* Receita Projetada */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #d1fae5',
              }}>
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem', textAlign: 'center' }}>
                  +
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#065f46',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  RECEITA PROJETADA
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#059669',
                  textAlign: 'center',
                }}>
                  {formatCurrency(forecast.projectedIncome)}
                </div>
              </div>

              {/* Despesa Projetada */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #fee2e2',
              }}>
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem', textAlign: 'center' }}>
                  -
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#991b1b',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  DESPESA PROJETADA
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#dc2626',
                  textAlign: 'center',
                }}>
                  {formatCurrency(forecast.projectedExpense)}
                </div>
              </div>

              {/* Rendimento Poupanca */}
              <div style={{
                backgroundColor: 'white',
                padding: '1.5rem',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                border: '2px solid #dbeafe',
              }}>
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem', textAlign: 'center' }}>
                  %
                </div>
                <div style={{
                  fontSize: '0.875rem',
                  color: '#1e40af',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  textAlign: 'center',
                }}>
                  RENDIMENTO POUPANCA
                </div>
                <div style={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#2563eb',
                  textAlign: 'center',
                }}>
                  {formatCurrency(forecast.projectedSavingsYield)}
                </div>
              </div>
            </div>

            {/* Comparativo Atual vs Projetado */}
            <h3 style={{ marginBottom: '1rem', color: '#374151' }}>Saldo Atual vs Projetado</h3>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
              gap: '1rem',
              marginBottom: '2rem',
            }}>
              {/* Saldo Atual */}
              <Card>
                <h4 style={{ marginTop: 0, color: '#6b7280', textAlign: 'center' }}>SALDO ATUAL</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#6b7280' }}>Conta:</span>
                    <span style={{ fontWeight: 'bold' }}>{formatCurrency(forecast.currentAccountBalance)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#6b7280' }}>Poupanca:</span>
                    <span style={{ fontWeight: 'bold' }}>{formatCurrency(forecast.currentSavingsBalance)}</span>
                  </div>
                  <hr style={{ margin: '0.5rem 0', border: 'none', borderTop: '1px solid #e5e7eb' }} />
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ fontWeight: 'bold', color: '#374151' }}>Total:</span>
                    <span style={{ fontWeight: 'bold', fontSize: '1.25rem', color: '#8b5cf6' }}>
                      {formatCurrency(forecast.currentTotalBalance)}
                    </span>
                  </div>
                </div>
              </Card>

              {/* Saldo Projetado */}
              <Card>
                <h4 style={{ marginTop: 0, color: '#6b7280', textAlign: 'center' }}>SALDO PROJETADO</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#6b7280' }}>Conta:</span>
                    <span style={{ fontWeight: 'bold' }}>{formatCurrency(forecast.projectedAccountBalance)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#6b7280' }}>Poupanca:</span>
                    <span style={{ fontWeight: 'bold' }}>{formatCurrency(forecast.projectedSavingsBalance)}</span>
                  </div>
                  <hr style={{ margin: '0.5rem 0', border: 'none', borderTop: '1px solid #e5e7eb' }} />
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ fontWeight: 'bold', color: '#374151' }}>Total:</span>
                    <span style={{ fontWeight: 'bold', fontSize: '1.25rem', color: '#8b5cf6' }}>
                      {formatCurrency(forecast.projectedTotalBalance)}
                    </span>
                  </div>
                </div>
              </Card>
            </div>

            {/* Variacao */}
            <div style={{
              backgroundColor: variation.isPositive ? '#d1fae5' : '#fee2e2',
              padding: '1.5rem',
              borderRadius: '8px',
              textAlign: 'center',
              marginBottom: '2rem',
            }}>
              <div style={{ fontSize: '0.875rem', color: variation.isPositive ? '#065f46' : '#991b1b', marginBottom: '0.5rem' }}>
                VARIACAO ESPERADA
              </div>
              <div style={{
                fontSize: '2rem',
                fontWeight: 'bold',
                color: variation.isPositive ? '#059669' : '#dc2626',
              }}>
                {variation.isPositive ? '+' : ''}{formatCurrency(variation.value)}
              </div>
              <div style={{
                fontSize: '0.875rem',
                color: variation.isPositive ? '#065f46' : '#991b1b',
                marginTop: '0.5rem',
              }}>
                {variation.isPositive
                  ? 'Seu patrimonio deve aumentar no proximo mes!'
                  : 'Atencao: suas despesas podem superar as receitas.'}
              </div>
            </div>

            {/* Historico */}
            {forecast.history && forecast.history.length > 0 && (
              <Card>
                <h3 style={{ marginTop: 0, color: '#374151' }}>Historico Mensal</h3>
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                      <tr style={{ backgroundColor: '#f3f4f6' }}>
                        <th style={{ padding: '0.75rem', textAlign: 'left', borderBottom: '2px solid #e5e7eb' }}>
                          Mes/Ano
                        </th>
                        <th style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '2px solid #e5e7eb' }}>
                          Receitas
                        </th>
                        <th style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '2px solid #e5e7eb' }}>
                          Despesas
                        </th>
                        <th style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '2px solid #e5e7eb' }}>
                          Poupanca
                        </th>
                        <th style={{ padding: '0.75rem', textAlign: 'right', borderBottom: '2px solid #e5e7eb' }}>
                          Saldo
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {forecast.history.map((item, index) => (
                        <tr key={index} style={{ borderBottom: '1px solid #e5e7eb' }}>
                          <td style={{ padding: '0.75rem' }}>
                            {getMonthName(item.month)}/{item.year}
                          </td>
                          <td style={{ padding: '0.75rem', textAlign: 'right', color: '#059669' }}>
                            {formatCurrency(item.income)}
                          </td>
                          <td style={{ padding: '0.75rem', textAlign: 'right', color: '#dc2626' }}>
                            {formatCurrency(item.expense)}
                          </td>
                          <td style={{ padding: '0.75rem', textAlign: 'right', color: '#2563eb' }}>
                            {formatCurrency(item.savingsDeposit)}
                          </td>
                          <td style={{
                            padding: '0.75rem',
                            textAlign: 'right',
                            fontWeight: 'bold',
                            color: item.balance >= 0 ? '#059669' : '#dc2626',
                          }}>
                            {formatCurrency(item.balance)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </Card>
            )}

            {/* Mensagem quando nao ha historico */}
            {(!forecast.history || forecast.history.length === 0) && (
              <Card>
                <div style={{
                  textAlign: 'center',
                  padding: '2rem',
                  color: '#6b7280',
                }}>
                  <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>i</div>
                  <h4 style={{ margin: '0 0 0.5rem 0' }}>Sem historico disponivel</h4>
                  <p style={{ margin: 0 }}>
                    Ainda nao ha transacoes registradas nos ultimos {months} meses para calcular a previsao.
                    Comece a registrar suas receitas e despesas!
                  </p>
                </div>
              </Card>
            )}
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default Forecast;
