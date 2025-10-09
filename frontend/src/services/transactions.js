import authAPI from './auth';

export const transactionsService = {
  async listTransactions(page = 0, size = 10, sort = 'transactionDate', order = 'desc') {
    try {
      const response = await authAPI.get('/v1/transactions', {
        headers: {
          page,
          size,
          sort,
          order,
        },
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao listar transações');
    }
  },

  async getTransactionById(id) {
    try {
      const response = await authAPI.get(`/v1/transactions/${id}`);
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao buscar transação';

      if (message.includes('Transação não encontrada')) {
        message = 'Transação não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para acessar esta transação';
      }

      throw new Error(message);
    }
  },

  async createTransaction(type, category, amount, transactionDate, description) {
    try {
      const response = await authAPI.post('/v1/transactions', {
        type,
        category,
        amount,
        transactionDate,
        description,
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao criar transação';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  async updateTransaction(id, type, category, amount, transactionDate, description) {
    try {
      const payload = {
        type,
        category,
        amount,
        transactionDate,
        description,
      };

      await authAPI.put(`/v1/transactions/${id}`, payload);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao atualizar transação';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      } else if (message.includes('Transação não encontrada')) {
        message = 'Transação não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para atualizar esta transação';
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  async deleteTransaction(id) {
    try {
      await authAPI.delete(`/v1/transactions/${id}`);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao deletar transação';

      if (message.includes('Transação não encontrada')) {
        message = 'Transação não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para deletar esta transação';
      }

      throw new Error(message);
    }
  },

  async getMonthlyBalance(year, month) {
    try {
      const response = await authAPI.get('/v1/transactions/balance', {
        params: {
          year,
          month,
        },
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao buscar saldo mensal');
    }
  },
};
