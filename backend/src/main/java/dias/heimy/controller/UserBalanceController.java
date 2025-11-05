package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.BALANCE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.UserBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Balance", description = "Endpoints para consulta de saldo consolidado")
@RequestMapping(value = BALANCE, produces = APPLICATION_JSON_VALUE)
public interface UserBalanceController {

    @Operation(
            summary = "Obter saldo consolidado atual do usuário",
            description =
                    "Retorna o saldo consolidado atual incluindo total de receitas, despesas, saldo em conta e poupança",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    ResponseEntity<UserBalanceResponse> getCurrentBalance(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Calcular saldo mensal",
            description = "Calcula o saldo de um mês específico baseado nas transações do período",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/monthly")
    ResponseEntity<MonthlyBalanceResponse> getMonthlyBalance(
            @Parameter(description = "Ano", example = "2025") @RequestParam int year,
            @Parameter(description = "Mês", example = "10") @RequestParam int month,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);
}
