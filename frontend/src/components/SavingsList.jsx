import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { savingsService } from '../services/savings';
import Header from './Header';
import Button from './common/Button';
import Card from './common/Card';
import { formatCurrency, formatDate } from '../utils/formatters';

const SavingsList = () => {
  const navigate = useNavigate();
  const [savings, setSavings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [savingToDelete, setSavingToDelete] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [consolidatedYield, setConsolidatedYield] = useState(null);

  const savingsTypeLabels = {
    EMERGENCY_FUND: 'Reserva de Emergência',
    RETIREMENT: 'Aposentadoria',
    TRAVEL: 'Viagem',
    INVESTMENT: 'Investimento',
    EDUCATION: 'Educação',
    HOME: 'Casa/Imóvel',
    VEHICLE: 'Veículo',
    OTHER: 'Outros',
  };

  useEffect(() => {
    document.title = 'PFT - Poupanças';
    loadSavings();
    loadConsolidatedYield();
  }, [page]);

  const loadSavings = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await savingsService.listSavings(page, 10, 'id', 'desc');
      setSavings(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadConsolidatedYield = async () => {
    try {
      const data = await savingsService.calculateConsolidatedYield();
      setConsolidatedYield(data);
    } catch (err) {
      console.error('Erro ao carregar rendimento consolidado:', err);
    }
  };

  const confirmDelete = (saving) => {
    setSavingToDelete(saving);
    setShowDeleteModal(true);
  };

  const handleDelete = async () => {
    if (!savingToDelete) return;

    try {
      setError('');
      await savingsService.deleteSavings(savingToDelete.id);
      setShowDeleteModal(false);
      setSavingToDelete(null);
      loadSavings();
      loadConsolidatedYield();
    } catch (err) {
      setError(err.message);
      setShowDeleteModal(false);
      setSavingToDelete(null);
    }
  };

  const getSavingsTypeBadgeStyle = (type) => {
    const styles = {
      EMERGENCY_FUND: { bg: '#fef3c7', color: '#92400e' },
      RETIREMENT: { bg: '#dbeafe', color: '#1e40af' },
      TRAVEL: { bg: '#fce7f3', color: '#9f1239' },
      INVESTMENT: { bg: '#d1fae5', color: '#065f46' },
      EDUCATION: { bg: '#e0e7ff', color: '#3730a3' },
      HOME: { bg: '#fed7aa', color: '#92400e' },
      VEHICLE: { bg: '#e9d5ff', color: '#6b21a8' },
      OTHER: { bg: '#f3f4f6', color: '#374151' },
    };
    return styles[type] || styles.OTHER;
  };

  // Calcula taxa anual composta: (1 + taxa_mensal/100)^12 - 1
  const calculateAnnualRate = (monthlyRate) => {
    return ((Math.pow(1 + monthlyRate / 100, 12) - 1) * 100).toFixed(2);
  };

  // Calcula projeção futura: valor * (1 + taxa/100)^meses
  const calculateFutureValue = (currentValue, monthlyRate, months) => {
    return currentValue * Math.pow(1 + monthlyRate / 100, months);
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h2 style={{ margin: 0 }}>Poupanças</h2>
          <Button onClick={() => navigate('/savings/new')} variant="primary">
            + Nova Poupança
          </Button>
        </div>

        {consolidatedYield && consolidatedYield.totalSavings > 0 && (
          <Card style={{ marginBottom: '2rem', backgroundColor: '#f0fdf4', border: '2px solid #10b981' }}>
            {/* Linha 1: Total e Taxas */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '1.5rem'
            }}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  💰 Total em Poupanças
                </div>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#059669' }}>
                  {formatCurrency(consolidatedYield.totalAmount)}
                </div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  📊 Taxa Média
                </div>
                <div style={{ fontSize: '1.125rem', fontWeight: 'bold', color: '#059669' }}>
                  {consolidatedYield.averageInterestRate}% a.m.
                </div>
                <div style={{ fontSize: '0.875rem', color: '#047857', marginTop: '0.25rem' }}>
                  ({calculateAnnualRate(consolidatedYield.averageInterestRate)}% a.a.)
                </div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  📈 Rendimento Mensal
                </div>
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#10b981' }}>
                  +{formatCurrency(consolidatedYield.totalMonthlyYield)}
                </div>
              </div>
            </div>

            {/* Linha 2: Projeções - OCULTO TEMPORARIAMENTE */}
            {/* <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '1.5rem',
              marginTop: '1.5rem',
              paddingTop: '1.5rem',
              borderTop: '1px solid #d1fae5'
            }}>
              <div style={{ textAlign: 'center', backgroundColor: '#ecfdf5', padding: '1rem', borderRadius: '8px' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  🎯 Projeção em 6 meses
                </div>
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#059669' }}>
                  {formatCurrency(calculateFutureValue(consolidatedYield.totalAmount, consolidatedYield.averageInterestRate, 6))}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#047857', marginTop: '0.25rem' }}>
                  +{formatCurrency(calculateFutureValue(consolidatedYield.totalAmount, consolidatedYield.averageInterestRate, 6) - consolidatedYield.totalAmount)}
                </div>
              </div>
              <div style={{ textAlign: 'center', backgroundColor: '#ecfdf5', padding: '1rem', borderRadius: '8px' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  🚀 Projeção em 12 meses
                </div>
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#059669' }}>
                  {formatCurrency(calculateFutureValue(consolidatedYield.totalAmount, consolidatedYield.averageInterestRate, 12))}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#047857', marginTop: '0.25rem' }}>
                  +{formatCurrency(calculateFutureValue(consolidatedYield.totalAmount, consolidatedYield.averageInterestRate, 12) - consolidatedYield.totalAmount)}
                </div>
              </div>
              <div style={{ textAlign: 'center', backgroundColor: '#d1fae5', padding: '1rem', borderRadius: '8px' }}>
                <div style={{ fontSize: '0.875rem', color: '#065f46', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                  💵 Após Próximo Rendimento
                </div>
                <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#059669' }}>
                  {formatCurrency(consolidatedYield.totalAfterYield)}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#047857', marginTop: '0.25rem' }}>
                  mês seguinte
                </div>
              </div>
            </div> */}
          </Card>
        )}

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

        <Card>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
              Carregando poupanças...
            </div>
          ) : savings.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '3rem' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🏦</div>
              <p style={{ color: '#6b7280', marginBottom: '1rem' }}>
                Nenhuma poupança cadastrada ainda.
              </p>
              <Button onClick={() => navigate('/savings/new')} variant="primary">
                Criar Primeira Poupança
              </Button>
            </div>
          ) : (
            <>
              <div style={{ overflowX: 'auto' }}>
                <table style={{
                  width: '100%',
                  borderCollapse: 'collapse',
                  fontSize: '0.875rem',
                }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid #e5e7eb' }}>
                      <th style={{ padding: '0.75rem', textAlign: 'left', fontWeight: 'bold', color: '#374151' }}>
                        Nome
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'left', fontWeight: 'bold', color: '#374151' }}>
                        Tipo
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'center', fontWeight: 'bold', color: '#374151' }}>
                        Data
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'right', fontWeight: 'bold', color: '#374151' }}>
                        Valor
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'center', fontWeight: 'bold', color: '#374151' }}>
                        Taxa
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'right', fontWeight: 'bold', color: '#374151' }}>
                        Rendimento Mensal
                      </th>
                      <th style={{ padding: '0.75rem', textAlign: 'center', fontWeight: 'bold', color: '#374151' }}>
                        Ações
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {savings.map((saving) => {
                      const badgeStyle = getSavingsTypeBadgeStyle(saving.type);
                      const monthlyYield = saving.amount * (saving.interestRate / 100);

                      return (
                        <tr key={saving.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                          <td style={{ padding: '1rem' }}>
                            <div style={{ fontWeight: 'bold', color: '#111827', marginBottom: '0.25rem' }}>
                              {saving.name}
                            </div>
                            {saving.description && (
                              <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>
                                {saving.description}
                              </div>
                            )}
                          </td>
                          <td style={{ padding: '1rem' }}>
                            <span style={{
                              padding: '0.25rem 0.75rem',
                              backgroundColor: badgeStyle.bg,
                              color: badgeStyle.color,
                              borderRadius: '12px',
                              fontSize: '0.875rem',
                              fontWeight: 'bold',
                              whiteSpace: 'nowrap',
                            }}>
                              {savingsTypeLabels[saving.type] || saving.type}
                            </span>
                          </td>
                          <td style={{ padding: '1rem', textAlign: 'center', color: '#6b7280' }}>
                            {formatDate(saving.transactionDate)}
                          </td>
                          <td style={{ padding: '1rem', textAlign: 'right', fontWeight: 'bold', color: '#2563eb' }}>
                            {formatCurrency(saving.amount)}
                          </td>
                          <td style={{ padding: '1rem', textAlign: 'center', fontWeight: 'bold', color: '#059669' }}>
                            {saving.interestRate}% a.m.
                          </td>
                          <td style={{ padding: '1rem', textAlign: 'right', fontWeight: 'bold', color: '#10b981' }}>
                            +{formatCurrency(monthlyYield)}
                          </td>
                          <td style={{ padding: '1rem' }}>
                            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
                              <Button
                                onClick={() => navigate(`/savings/${saving.id}/edit`)}
                                variant="success"
                                size="small"
                              >
                                ✏️ Editar
                              </Button>
                              <Button
                                onClick={() => confirmDelete(saving)}
                                variant="danger"
                                size="small"
                              >
                                🗑 Deletar
                              </Button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div style={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  gap: '1rem',
                  marginTop: '2rem',
                  paddingTop: '1rem',
                  borderTop: '1px solid #e5e7eb',
                }}>
                  <Button
                    onClick={() => setPage(page - 1)}
                    disabled={page === 0}
                    variant="secondary"
                    size="small"
                  >
                    ← Anterior
                  </Button>
                  <span style={{ color: '#6b7280' }}>
                    Página {page + 1} de {totalPages}
                  </span>
                  <Button
                    onClick={() => setPage(page + 1)}
                    disabled={page === totalPages - 1}
                    variant="secondary"
                    size="small"
                  >
                    Próxima →
                  </Button>
                </div>
              )}
            </>
          )}
        </Card>
      </div>

      {showDeleteModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000,
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '2rem',
            borderRadius: '8px',
            maxWidth: '400px',
            width: '90%',
          }}>
            <h3 style={{ marginTop: 0 }}>Confirmar Exclusão</h3>
            <p style={{ marginBottom: '1rem' }}>
              Tem certeza que deseja deletar a poupança <strong>{savingToDelete?.name}</strong>?
            </p>
            <p style={{ marginBottom: '1.5rem', color: '#059669', fontWeight: 'bold' }}>
              O valor de {formatCurrency(savingToDelete?.amount || 0)} será devolvido para sua conta.
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
              <Button
                onClick={() => {
                  setShowDeleteModal(false);
                  setSavingToDelete(null);
                }}
                variant="secondary"
                size="small"
              >
                Cancelar
              </Button>
              <Button
                onClick={handleDelete}
                variant="danger"
                size="small"
              >
                Confirmar Exclusão
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SavingsList;
