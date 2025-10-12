import React, { useState, useEffect } from 'react';
import { transactionsService } from '../services/transactions';
import { useNavigate } from 'react-router-dom';
import Header from './Header';
import Button from './common/Button';
import Card from './common/Card';
import { colors } from '../theme/colors';
import { formatCurrency, formatDate } from '../utils/formatters';

const TransactionsList = () => {
  const navigate = useNavigate();
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [transactionToDelete, setTransactionToDelete] = useState(null);

  useEffect(() => {
    loadTransactions();
  }, [page]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await transactionsService.listTransactions(page, 10);
      setTransactions(response.content);
      setTotalPages(Math.ceil(response.elements / response.pageSize));
      setTotalElements(response.elements);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!transactionToDelete) return;

    try {
      await transactionsService.deleteTransaction(transactionToDelete.id);
      setShowDeleteModal(false);
      setTransactionToDelete(null);
      loadTransactions();
    } catch (err) {
      alert(err.message);
      setShowDeleteModal(false);
      setTransactionToDelete(null);
    }
  };

  const confirmDelete = (transaction) => {
    setTransactionToDelete(transaction);
    setShowDeleteModal(true);
  };


  if (loading) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center' }}>
        <p>Carregando transações...</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h2 style={{ margin: 0 }}>Minhas Transações</h2>
          <Button onClick={() => navigate('/transactions/new')} variant="primary">
            + Nova Transação
          </Button>
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

        <div style={{
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          overflow: 'hidden',
        }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead style={{ backgroundColor: '#f3f4f6' }}>
              <tr>
                <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Data</th>
                <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Tipo</th>
                <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Categoria</th>
                <th style={{ padding: '1rem', textAlign: 'right', fontWeight: 'bold' }}>Valor</th>
                <th style={{ padding: '1rem', textAlign: 'left', fontWeight: 'bold' }}>Descrição</th>
                <th style={{ padding: '1rem', textAlign: 'center', fontWeight: 'bold' }}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => (
                <tr key={transaction.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '1rem', color: '#6b7280' }}>
                    {formatDate(transaction.transactionDate)}
                  </td>
                  <td style={{ padding: '1rem' }}>
                    <span style={{
                      padding: '0.25rem 0.75rem',
                      backgroundColor: transaction.type === 'INCOME' ? '#d1fae5' : '#fee2e2',
                      color: transaction.type === 'INCOME' ? '#065f46' : '#991b1b',
                      borderRadius: '12px',
                      fontSize: '0.875rem',
                      fontWeight: 'bold',
                    }}>
                      {transaction.type === 'INCOME' ? 'Receita' : 'Despesa'}
                    </span>
                  </td>
                  <td style={{ padding: '1rem', fontWeight: '500' }}>{transaction.category}</td>
                  <td style={{
                    padding: '1rem',
                    textAlign: 'right',
                    fontWeight: 'bold',
                    color: transaction.type === 'INCOME' ? '#059669' : '#dc2626',
                  }}>
                    {transaction.type === 'INCOME' ? '+' : '-'} {formatCurrency(transaction.amount)}
                  </td>
                  <td style={{ padding: '1rem', color: '#6b7280', maxWidth: '200px' }}>
                    {transaction.description ? (
                      transaction.description.length > 50
                        ? transaction.description.substring(0, 50) + '...'
                        : transaction.description
                    ) : '-'}
                  </td>
                  <td style={{ padding: '1rem', textAlign: 'center' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
                      <Button
                        onClick={() => navigate(`/transactions/${transaction.id}/edit`)}
                        variant="success"
                        size="small"
                      >
                        ✏️ Editar
                      </Button>
                      <Button
                        onClick={() => confirmDelete(transaction)}
                        variant="danger"
                        size="small"
                      >
                        🗑 Deletar
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {transactions.length === 0 && (
            <div style={{ padding: '2rem', textAlign: 'center', color: '#6b7280' }}>
              Nenhuma transação encontrada. Clique em "Nova Transação" para começar.
            </div>
          )}
        </div>

        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '1rem', alignItems: 'center' }}>
            <Button
              onClick={() => setPage(Math.max(0, page - 1))}
              disabled={page === 0}
              variant="primary"
              size="small"
            >
              Anterior
            </Button>
            <span style={{ padding: '0.5rem 1rem' }}>
              Página {page + 1} de {totalPages} ({totalElements} transações)
            </span>
            <Button
              onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
              disabled={page >= totalPages - 1}
              variant="primary"
              size="small"
            >
              Próxima
            </Button>
          </div>
        )}

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
              <h3 style={{ marginTop: 0 }}>Confirmar Deleção</h3>
              <p>Tem certeza que deseja deletar a transação <strong>{transactionToDelete?.category}</strong> de {transactionToDelete?.amount && formatCurrency(transactionToDelete.amount)}?</p>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <Button
                  onClick={() => {
                    setShowDeleteModal(false);
                    setTransactionToDelete(null);
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
                  Deletar
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TransactionsList;
