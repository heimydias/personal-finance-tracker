export const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
};

export const formatDate = (dateString) => {
  return new Date(dateString + 'T00:00:00').toLocaleDateString('pt-BR');
};

export const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('pt-BR');
};

export const getMonthName = (monthNumber) => {
  const months = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];
  return months[monthNumber - 1];
};
