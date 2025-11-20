package dias.heimy.service;

import dias.heimy.dto.response.ForecastResponse;

public interface ForecastService {

    /**
     * Calcula a previsão financeira para o próximo mês baseado na média dos meses anteriores.
     *
     * @param monthsToAnalyze número de meses a analisar (1-12)
     * @param authorizationHeader token JWT do usuário
     * @return previsão financeira com médias, projeções e histórico
     */
    ForecastResponse calculateForecast(int monthsToAnalyze, String authorizationHeader);
}
