import authAPI from './auth';

export const balanceService = {
  /**
   * Obtém o saldo consolidado atual do usuário
   * Endpoint: GET /api/v1/balance
   *
   * @returns {Promise<UserBalanceResponse>} Saldo consolidado incluindo:
   * - totalIncome: Total de receitas
   * - totalExpense: Total de despesas
   * - accountBalance: Saldo em conta corrente
   * - savingsBalance: Saldo em poupança
   * - totalBalance: Saldo total (conta + poupança)
   */
  async getCurrentBalance() {
    try {
      const response = await authAPI.get('/v1/balance');
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao buscar saldo atual');
    }
  },

  /**
   * Calcula o saldo de um mês específico
   * Endpoint: GET /api/v1/balance/monthly
   *
   * @param {number} year - Ano
   * @param {number} month - Mês (1-12)
   * @returns {Promise<MonthlyBalanceResponse>} Saldo mensal
   */
  async getMonthlyBalance(year, month) {
    try {
      const response = await authAPI.get('/v1/balance/monthly', {
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
