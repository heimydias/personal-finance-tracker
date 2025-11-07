package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.UserBalanceResponse;
import dias.heimy.service.UserBalanceService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UserBalanceControllerImpl")
class UserBalanceControllerImplTest {

    @Mock
    private UserBalanceService userBalanceService;

    @InjectMocks
    private UserBalanceControllerImpl userBalanceController;

    @Test
    @DisplayName("Should get current balance successfully")
    void shouldGetCurrentBalance() {

        var expectedResponse = new UserBalanceResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("5000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("3500.00"),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(userBalanceService.getCurrentBalance("Bearer token")).thenReturn(expectedResponse);

        var result = userBalanceController.getCurrentBalance("Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(userBalanceService).getCurrentBalance("Bearer token");
    }

    @Test
    @DisplayName("Should get monthly balance successfully")
    void shouldGetMonthlyBalance() {

        var year = 2024;
        var month = 10;
        var expectedResponse = MonthlyBalanceResponse.of(
                year,
                month,
                new BigDecimal("3000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("500.00"),
                BigDecimal.ZERO,
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"));

        when(userBalanceService.getMonthlyBalance(year, month, "Bearer token")).thenReturn(expectedResponse);

        var result = userBalanceController.getMonthlyBalance(year, month, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(userBalanceService).getMonthlyBalance(year, month, "Bearer token");
    }

    @Test
    @DisplayName("Should verify response contains correct balance values")
    void shouldVerifyResponseContainsCorrectBalanceValues() {

        var expectedResponse = new UserBalanceResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("10000.00"),
                new BigDecimal("4000.00"),
                new BigDecimal("6000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("7000.00"),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(userBalanceService.getCurrentBalance("Bearer token")).thenReturn(expectedResponse);

        var result = userBalanceController.getCurrentBalance("Bearer token");

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().totalIncome()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(result.getBody().totalExpense()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(result.getBody().accountBalance()).isEqualByComparingTo(new BigDecimal("6000.00"));
        assertThat(result.getBody().savingsBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.getBody().totalBalance()).isEqualByComparingTo(new BigDecimal("7000.00"));
    }
}
