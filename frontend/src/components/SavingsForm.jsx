import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { savingsService } from '../services/savings';
import Header from './Header';
import Button from './common/Button';
import Card from './common/Card';

const SavingsForm = ({ isNew }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(!isNew);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState([]);

  const [formData, setFormData] = useState({
    name: '',
    type: 'EMERGENCY_FUND',
    amount: '',
    interestRate: '',
    description: '',
    transactionDate: new Date().toISOString().split('T')[0],
  });

  const savingsTypes = [
    { value: 'EMERGENCY_FUND', label: 'Reserva de Emergência' },
    { value: 'RETIREMENT', label: 'Aposentadoria' },
    { value: 'TRAVEL', label: 'Viagem' },
    { value: 'INVESTMENT', label: 'Investimento' },
    { value: 'EDUCATION', label: 'Educação' },
    { value: 'HOME', label: 'Casa/Imóvel' },
    { value: 'VEHICLE', label: 'Veículo' },
    { value: 'OTHER', label: 'Outros' },
  ];

  useEffect(() => {
    document.title = isNew ? 'PFT - Nova Poupança' : 'PFT - Editar Poupança';
    if (!isNew && id) {
      loadSavings();
    }
  }, [id, isNew]);

  const loadSavings = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await savingsService.getSavingsById(id);
      setFormData({
        name: data.name,
        type: data.type,
        amount: data.amount.toString(),
        interestRate: data.interestRate.toString(),
        description: data.description || '',
        transactionDate: data.transactionDate,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError('');
      setFieldErrors([]);

      const amount = parseFloat(formData.amount);
      const interestRate = parseFloat(formData.interestRate);

      if (isNew) {
        await savingsService.createSavings(
          formData.name,
          formData.type,
          amount,
          interestRate,
          formData.description,
          formData.transactionDate
        );
      } else {
        await savingsService.updateSavings(
          id,
          formData.name,
          formData.type,
          amount,
          interestRate,
          formData.description,
          formData.transactionDate
        );
      }

      navigate('/savings');
    } catch (err) {
      if (err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
      } else {
        setError(err.message);
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f0f9ff' }}>
      <Header />
      <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ marginBottom: '2rem' }}>
          <Button onClick={() => navigate('/savings')} variant="secondary" size="small">
            ← Voltar
          </Button>
        </div>

        <Card>
          <h2 style={{ marginTop: 0, marginBottom: '2rem' }}>
            {isNew ? 'Nova Poupança' : 'Editar Poupança'}
          </h2>

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

          {fieldErrors.length > 0 && (
            <div style={{
              padding: '1rem',
              backgroundColor: '#fee2e2',
              color: '#dc2626',
              borderRadius: '6px',
              marginBottom: '1rem',
            }}>
              <ul style={{ margin: 0, paddingLeft: '1.5rem' }}>
                {fieldErrors.map((err, idx) => (
                  <li key={idx}>{err}</li>
                ))}
              </ul>
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
              Carregando...
            </div>
          ) : (
            <form onSubmit={handleSubmit}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{
                  display: 'block',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  color: '#374151',
                }}>
                  Nome *
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  placeholder="Ex: Reserva de Emergência"
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
                  Tipo *
                </label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  required
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
                  {savingsTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.5rem' }}>
                <div>
                  <label style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                    color: '#374151',
                  }}>
                    Valor (R$) *
                  </label>
                  <input
                    type="number"
                    name="amount"
                    value={formData.amount}
                    onChange={handleChange}
                    required
                    min="0.01"
                    step="0.01"
                    placeholder="0.00"
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

                <div>
                  <label style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                    color: '#374151',
                  }}>
                    Taxa de Rendimento (% a.m.) *
                  </label>
                  <input
                    type="number"
                    name="interestRate"
                    value={formData.interestRate}
                    onChange={handleChange}
                    required
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    style={{
                      width: '100%',
                      padding: '0.75rem',
                      border: '1px solid #d1d5db',
                      borderRadius: '6px',
                      fontSize: '1rem',
                      boxSizing: 'border-box',
                    }}
                  />
                  <div style={{ fontSize: '0.75rem', color: '#6b7280', marginTop: '0.25rem' }}>
                    Exemplo: 0.5 para 0,5% ao mês
                  </div>
                </div>
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
                  name="transactionDate"
                  value={formData.transactionDate}
                  onChange={handleChange}
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

              <div style={{ marginBottom: '2rem' }}>
                <label style={{
                  display: 'block',
                  fontWeight: 'bold',
                  marginBottom: '0.5rem',
                  color: '#374151',
                }}>
                  Descrição
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows="4"
                  placeholder="Informações adicionais sobre esta poupança..."
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontSize: '1rem',
                    boxSizing: 'border-box',
                    resize: 'vertical',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              {!isNew && formData.amount && formData.interestRate && (
                <div style={{
                  backgroundColor: '#f0fdf4',
                  padding: '1rem',
                  borderRadius: '6px',
                  marginBottom: '1.5rem',
                  border: '1px solid #10b981',
                }}>
                  <div style={{ fontSize: '0.875rem', color: '#065f46', marginBottom: '0.5rem' }}>
                    <strong>Projeção de Rendimento:</strong>
                  </div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#059669' }}>
                    +R$ {(parseFloat(formData.amount) * (parseFloat(formData.interestRate) / 100)).toFixed(2)} por mês
                  </div>
                </div>
              )}

              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <Button
                  type="button"
                  onClick={() => navigate('/savings')}
                  variant="secondary"
                  disabled={saving}
                >
                  Cancelar
                </Button>
                <Button type="submit" variant="primary" disabled={saving}>
                  {saving ? 'Salvando...' : isNew ? 'Criar Poupança' : 'Salvar Alterações'}
                </Button>
              </div>
            </form>
          )}
        </Card>
      </div>
    </div>
  );
};

export default SavingsForm;
