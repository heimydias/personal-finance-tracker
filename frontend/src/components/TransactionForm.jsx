import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { transactionsService } from '../services/transactions';

const TransactionForm = ({ isNew = false }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [type, setType] = useState('INCOME');
  const [category, setCategory] = useState('');
  const [amount, setAmount] = useState('');
  const [transactionDate, setTransactionDate] = useState(new Date().toISOString().split('T')[0]);
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [loadingTransaction, setLoadingTransaction] = useState(!isNew);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!isNew && id) {
      loadTransaction();
    }
  }, [id, isNew]);

  const loadTransaction = async () => {
    try {
      setLoadingTransaction(true);
      setError('');
      const data = await transactionsService.getTransactionById(id);
      setType(data.type);
      setCategory(data.category);
      setAmount(data.amount);
      setTransactionDate(data.transactionDate);
      setDescription(data.description || '');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoadingTransaction(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!amount || parseFloat(amount) <= 0) {
      setError('O valor deve ser maior que zero');
      return;
    }

    try {
      setLoading(true);
      if (isNew) {
        await transactionsService.createTransaction(
          type,
          category,
          parseFloat(amount),
          transactionDate,
          description || null
        );
        setSuccess('Transação criada com sucesso!');
        setTimeout(() => navigate('/transactions'), 1500);
      } else {
        await transactionsService.updateTransaction(
          id,
          type,
          category,
          parseFloat(amount),
          transactionDate,
          description || null
        );
        setSuccess('Transação atualizada com sucesso!');
        setTimeout(() => navigate('/transactions'), 1500);
      }
    } catch (err) {
      setError(err.fieldErrors || err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loadingTransaction) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center' }}>
        <p>Carregando transação...</p>
      </div>
    );
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
      <div style={{
        backgroundColor: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        padding: '2rem',
      }}>
        <h2 style={{ marginTop: 0, marginBottom: '2rem' }}>
          {isNew ? 'Nova Transação' : 'Editar Transação'}
        </h2>

        {error && (
          <div style={{
            padding: '1rem',
            backgroundColor: '#fee2e2',
            color: '#dc2626',
            borderRadius: '6px',
            marginBottom: '1rem',
          }}>
            {Array.isArray(error) ? (
              <ul style={{ margin: 0, paddingLeft: '20px' }}>
                {error.map((err, index) => (
                  <li key={index} style={{ marginBottom: '4px' }}>
                    {err}
                  </li>
                ))}
              </ul>
            ) : (
              error
            )}
          </div>
        )}

        {success && (
          <div style={{
            padding: '1rem',
            backgroundColor: '#d1fae5',
            color: '#065f46',
            borderRadius: '6px',
            marginBottom: '1rem',
          }}>
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Tipo *
            </label>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                <input
                  type="radio"
                  value="INCOME"
                  checked={type === 'INCOME'}
                  onChange={(e) => setType(e.target.value)}
                  style={{ marginRight: '0.5rem' }}
                />
                <span style={{ color: '#059669', fontWeight: 'bold' }}>Receita</span>
              </label>
              <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                <input
                  type="radio"
                  value="EXPENSE"
                  checked={type === 'EXPENSE'}
                  onChange={(e) => setType(e.target.value)}
                  style={{ marginRight: '0.5rem' }}
                />
                <span style={{ color: '#dc2626', fontWeight: 'bold' }}>Despesa</span>
              </label>
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Categoria *
            </label>
            <input
              type="text"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
              placeholder="Ex: Salário, Alimentação, Transporte"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Valor *
            </label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
              placeholder="0.00"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Data *
            </label>
            <input
              type="date"
              value={transactionDate}
              onChange={(e) => setTransactionDate(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
              }}
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{
              display: 'block',
              fontWeight: 'bold',
              marginBottom: '0.5rem',
              color: '#374151',
            }}>
              Descrição
            </label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows="3"
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '1rem',
                boxSizing: 'border-box',
                resize: 'vertical',
              }}
              placeholder="Descrição adicional (opcional)"
            />
          </div>

          <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: loading ? '#9ca3af' : '#2563eb',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontWeight: 'bold',
                fontSize: '1rem',
              }}
            >
              {loading ? 'Salvando...' : (isNew ? 'Criar Transação' : 'Salvar Alterações')}
            </button>
            <button
              type="button"
              onClick={() => navigate('/transactions')}
              disabled={loading}
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: '#6b7280',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontWeight: 'bold',
                fontSize: '1rem',
              }}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TransactionForm;
