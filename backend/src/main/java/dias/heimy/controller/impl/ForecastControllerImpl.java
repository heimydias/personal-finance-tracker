package dias.heimy.controller.impl;

import dias.heimy.controller.ForecastController;
import dias.heimy.dto.response.ForecastResponse;
import dias.heimy.service.ForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForecastControllerImpl implements ForecastController {

    private final ForecastService forecastService;

    @Override
    public ResponseEntity<ForecastResponse> getForecast(int months, String authorizationHeader) {
        log.info("Requisição para calcular previsão financeira com {} meses de histórico", months);
        ForecastResponse response = forecastService.calculateForecast(months, authorizationHeader);
        return ResponseEntity.ok(response);
    }
}
