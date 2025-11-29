package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.dto.response.ForecastResponse;
import dias.heimy.dto.response.MonthlyHistoryResponse;
import dias.heimy.service.ForecastService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ForecastControllerImpl")
class ForecastControllerImplTest {

    @Mock
    private ForecastService forecastService;

    @InjectMocks
    private ForecastControllerImpl forecastController;

    @Test
    @DisplayName("Should get forecast successfully with default months")
    void shouldGetForecast_WithDefaultMonths() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(3, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(3, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(forecastService).calculateForecast(3, "Bearer token");
    }

    @Test
    @DisplayName("Should get forecast with custom months parameter")
    void shouldGetForecast_WithCustomMonthsParameter() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(6, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(6, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(forecastService).calculateForecast(6, "Bearer token");
    }

    @Test
    @DisplayName("Should verify response contains correct forecast values")
    void shouldVerifyResponseContainsCorrectForecastValues() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(3, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(3, "Bearer token");

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().targetYear()).isEqualTo(expectedResponse.targetYear());
        assertThat(result.getBody().targetMonth()).isEqualTo(expectedResponse.targetMonth());
        assertThat(result.getBody().monthsAnalyzed()).isEqualTo(expectedResponse.monthsAnalyzed());
        assertThat(result.getBody().averageIncome()).isEqualByComparingTo(expectedResponse.averageIncome());
        assertThat(result.getBody().averageExpense()).isEqualByComparingTo(expectedResponse.averageExpense());
        assertThat(result.getBody().projectedIncome()).isEqualByComparingTo(expectedResponse.projectedIncome());
        assertThat(result.getBody().projectedExpense()).isEqualByComparingTo(expectedResponse.projectedExpense());
        assertThat(result.getBody().projectedSavingsYield())
                .isEqualByComparingTo(expectedResponse.projectedSavingsYield());
        assertThat(result.getBody().projectedTotalBalance())
                .isEqualByComparingTo(expectedResponse.projectedTotalBalance());
    }

    @Test
    @DisplayName("Should verify response contains history list")
    void shouldVerifyResponseContainsHistoryList() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(3, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(3, "Bearer token");

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().history()).isNotNull();
        assertThat(result.getBody().history()).hasSize(3);
    }

    @Test
    @DisplayName("Should get forecast with 12 months parameter")
    void shouldGetForecast_WithTwelveMonthsParameter() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(12, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(12, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(forecastService).calculateForecast(12, "Bearer token");
    }

    @Test
    @DisplayName("Should get forecast with 1 month parameter")
    void shouldGetForecast_WithOneMonthParameter() {
        var expectedResponse = createTestForecastResponse();

        when(forecastService.calculateForecast(1, "Bearer token")).thenReturn(expectedResponse);

        var result = forecastController.getForecast(1, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(forecastService).calculateForecast(1, "Bearer token");
    }

    private ForecastResponse createTestForecastResponse() {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);

        var history = List.of(
                MonthlyHistoryResponse.of(
                        2024, 10, new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00")),
                MonthlyHistoryResponse.of(
                        2024, 9, new BigDecimal("3200.00"), new BigDecimal("1400.00"), new BigDecimal("600.00")),
                MonthlyHistoryResponse.of(
                        2024, 8, new BigDecimal("2800.00"), new BigDecimal("1600.00"), new BigDecimal("400.00")));

        return new ForecastResponse(
                nextMonth.getYear(),
                nextMonth.getMonthValue(),
                3,
                new BigDecimal("3000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("50.00"),
                new BigDecimal("4500.00"),
                new BigDecimal("550.00"),
                new BigDecimal("5050.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("3500.00"),
                history);
    }
}
