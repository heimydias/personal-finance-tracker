import authAPI from './auth';

export const forecastService = {
  /**
   * Calcula a previsão financeira para o próximo mês
   * Endpoint: GET /api/v1/forecast
   *
   * @param {number} months - Quantidade de meses a analisar (1-12, default: 3)
   * @returns {Promise<ForecastResponse>} Previsão financeira incluindo:
   * - targetYear: Ano alvo da previsão
   * - targetMonth: Mês alvo da previsão
   * - monthsAnalyzed: Quantidade de meses analisados
   * - averageIncome: Média de receitas
   * - averageExpense: Média de despesas
   * - averageSavingsDeposit: Média de depósitos em poupança
   * - projectedIncome: Receita projetada
   * - projectedExpense: Despesa projetada
   * - projectedSavingsYield: Rendimento projetado das poupanças
   * - projectedAccountBalance: Saldo em conta projetado
   * - projectedSavingsBalance: Saldo em poupança projetado
   * - projectedTotalBalance: Saldo total projetado
   * - currentAccountBalance: Saldo atual em conta
   * - currentSavingsBalance: Saldo atual em poupança
   * - currentTotalBalance: Saldo total atual
   * - history: Histórico dos meses analisados
   */
  async getForecast(months = 3) {
    try {
      const response = await authAPI.get('/v1/forecast', {
        params: { months },
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao calcular previsão');
    }
  },
};
