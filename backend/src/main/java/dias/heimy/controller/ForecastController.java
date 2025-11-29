package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.FORECAST;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.response.ForecastResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Forecast", description = "Endpoints para previsão financeira")
@RequestMapping(value = FORECAST, produces = APPLICATION_JSON_VALUE)
public interface ForecastController {

    @Operation(
            summary = "Calcular previsão financeira para o próximo mês",
            description = "Calcula a previsão de receitas, despesas e saldo para o próximo mês "
                    + "baseado na média dos meses anteriores. Inclui rendimento projetado das poupanças.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    ResponseEntity<ForecastResponse> getForecast(
            @Parameter(description = "Quantidade de meses a analisar (1-12)", example = "3")
                    @RequestParam(defaultValue = "3")
                    int months,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);
}
