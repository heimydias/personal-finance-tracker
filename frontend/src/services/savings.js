import authAPI from './auth';

export const savingsService = {
  /**
   * Lista todas as poupanças do usuário
   * @param {number} page - Número da página (0-indexed)
   * @param {number} size - Tamanho da página
   * @param {string} sort - Campo de ordenação
   * @param {string} order - Direção (asc ou desc)
   */
  async listSavings(page = 0, size = 10, sort = 'id', order = 'desc') {
    try {
      const response = await authAPI.get('/v1/savings', {
        params: {
          page,
          size,
          sort,
        },
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao listar poupanças');
    }
  },

  /**
   * Busca uma poupança por ID
   * @param {string} id - UUID da poupança
   */
  async getSavingsById(id) {
    try {
      const response = await authAPI.get(`/v1/savings/${id}`);
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao buscar poupança';

      if (message.includes('não encontrada')) {
        message = 'Poupança não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para acessar esta poupança';
      }

      throw new Error(message);
    }
  },

  /**
   * Cria nova poupança
   * @param {string} name - Nome da poupança
   * @param {string} type - Tipo (EMERGENCY_FUND, RETIREMENT, etc.)
   * @param {number} amount - Valor inicial
   * @param {number} interestRate - Taxa de rendimento mensal (%)
   * @param {string} description - Descrição
   * @param {string} transactionDate - Data da transação (formato: YYYY-MM-DD)
   */
  async createSavings(name, type, amount, interestRate, description, transactionDate) {
    try {
      const response = await authAPI.post('/v1/savings', {
        name,
        type,
        amount,
        interestRate,
        description,
        transactionDate,
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao criar poupança';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  /**
   * Atualiza uma poupança existente
   * @param {string} id - UUID da poupança
   * @param {string} name - Nome da poupança
   * @param {string} type - Tipo
   * @param {number} amount - Valor
   * @param {number} interestRate - Taxa de rendimento
   * @param {string} description - Descrição
   * @param {string} transactionDate - Data da transação (formato: YYYY-MM-DD)
   */
  async updateSavings(id, name, type, amount, interestRate, description, transactionDate) {
    try {
      const payload = {
        name,
        type,
        amount,
        interestRate,
        description,
        transactionDate,
      };

      await authAPI.put(`/v1/savings/${id}`, payload);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao atualizar poupança';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      } else if (message.includes('não encontrada')) {
        message = 'Poupança não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para atualizar esta poupança';
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  /**
   * Deleta uma poupança (retorna o valor para a conta)
   * @param {string} id - UUID da poupança
   */
  async deleteSavings(id) {
    try {
      await authAPI.delete(`/v1/savings/${id}`);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao deletar poupança';

      if (message.includes('não encontrada')) {
        message = 'Poupança não encontrada';
      } else if (message.includes('não tem permissão')) {
        message = 'Você não tem permissão para deletar esta poupança';
      }

      throw new Error(message);
    }
  },

  /**
   * Calcula o rendimento mensal de uma poupança específica
   * @param {string} id - UUID da poupança
   */
  async calculateMonthlyYield(id) {
    try {
      const response = await authAPI.get(`/v1/savings/${id}/yield`);
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao calcular rendimento');
    }
  },

  /**
   * Calcula o rendimento consolidado de todas as poupanças
   */
  async calculateConsolidatedYield() {
    try {
      const response = await authAPI.get('/v1/savings/yield');
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao calcular rendimento consolidado');
    }
  },
};
