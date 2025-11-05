package dias.heimy.controller.impl;

import dias.heimy.controller.UserBalanceController;
import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.UserBalanceResponse;
import dias.heimy.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserBalanceControllerImpl implements UserBalanceController {

    private final UserBalanceService userBalanceService;

    @Override
    public ResponseEntity<UserBalanceResponse> getCurrentBalance(String authorizationHeader) {
        log.info("Requisição para consultar saldo consolidado");
        UserBalanceResponse response = userBalanceService.getCurrentBalance(authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MonthlyBalanceResponse> getMonthlyBalance(int year, int month, String authorizationHeader) {
        log.info("Requisição para calcular saldo mensal: {}/{}", year, month);
        MonthlyBalanceResponse response = userBalanceService.getMonthlyBalance(year, month, authorizationHeader);
        return ResponseEntity.ok(response);
    }
}
