package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.domain.enums.SavingsType;
import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.ConsolidatedYieldResponse;
import dias.heimy.dto.response.MonthlyYieldResponse;
import dias.heimy.dto.response.SavingsResponse;
import dias.heimy.service.SavingsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for SavingsControllerImpl")
class SavingsControllerImplTest {

    @Mock
    private SavingsService savingsService;

    @InjectMocks
    private SavingsControllerImpl savingsController;

    @Test
    @DisplayName("Should create savings successfully")
    void shouldCreateSavings_WhenValidData() {

        var request = new SavingsRequest(
                "Reserva de Emergência",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Reserva para emergências",
                LocalDate.now());
        var expectedResponse = createSavingsResponse();

        when(savingsService.createSavings(any(SavingsRequest.class), anyString()))
                .thenReturn(expectedResponse);

        var result = savingsController.createSavings(request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(savingsService).createSavings(request, "Bearer token");
    }

    @Test
    @DisplayName("Should get savings by ID successfully")
    void shouldGetSavingsById_WhenValidId() {

        var savingsId = UUID.randomUUID();
        var expectedResponse = createSavingsResponse();

        when(savingsService.getSavingsById(savingsId, "Bearer token")).thenReturn(expectedResponse);

        var result = savingsController.getSavingsById(savingsId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(savingsService).getSavingsById(savingsId, "Bearer token");
    }

    @Test
    @DisplayName("Should list savings successfully with pagination")
    void shouldListSavings_WhenValidPagination() {

        var pageable = PageRequest.of(0, 10);
        var savingsResponse = createSavingsResponse();
        var savingsPage = new PageImpl<>(List.of(savingsResponse), pageable, 1);

        when(savingsService.listSavings(any(Pageable.class), anyString())).thenReturn(savingsPage);

        var result = savingsController.listSavings(0, 10, "name", "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().content()).hasSize(1);
        assertThat(result.getBody().pageNumber()).isZero();
        assertThat(result.getBody().pageSize()).isEqualTo(10);
        assertThat(result.getBody().elements()).isEqualTo(1);
        verify(savingsService).listSavings(any(Pageable.class), eq("Bearer token"));
    }

    @Test
    @DisplayName("Should update savings successfully")
    void shouldUpdateSavings_WhenValidData() {

        var savingsId = UUID.randomUUID();
        var request = new SavingsRequest(
                "Reserva Atualizada",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1500.00"),
                new BigDecimal("2.0"),
                "Reserva atualizada",
                LocalDate.now());
        var expectedResponse = createSavingsResponse();

        when(savingsService.updateSavings(savingsId, request, "Bearer token")).thenReturn(expectedResponse);

        var result = savingsController.updateSavings(savingsId, request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(savingsService).updateSavings(savingsId, request, "Bearer token");
    }

    @Test
    @DisplayName("Should delete savings successfully")
    void shouldDeleteSavings_WhenValidId() {

        var savingsId = UUID.randomUUID();

        doNothing().when(savingsService).deleteSavings(savingsId, "Bearer token");

        var result = savingsController.deleteSavings(savingsId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(savingsService).deleteSavings(savingsId, "Bearer token");
    }

    @Test
    @DisplayName("Should calculate monthly yield successfully")
    void shouldCalculateMonthlyYield() {

        var savingsId = UUID.randomUUID();
        var expectedResponse = new MonthlyYieldResponse(
                savingsId,
                "Reserva de Emergência",
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                new BigDecimal("15.00"),
                new BigDecimal("1015.00"));

        when(savingsService.calculateMonthlyYield(savingsId, "Bearer token")).thenReturn(expectedResponse);

        var result = savingsController.calculateMonthlyYield(savingsId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(savingsService).calculateMonthlyYield(savingsId, "Bearer token");
    }

    @Test
    @DisplayName("Should calculate consolidated yield successfully")
    void shouldCalculateConsolidatedYield() {

        var expectedResponse = ConsolidatedYieldResponse.of(
                2,
                new BigDecimal("3000.00"),
                new BigDecimal("1.75"),
                new BigDecimal("52.50"),
                new BigDecimal("3052.50"),
                List.of());

        when(savingsService.calculateConsolidatedYield("Bearer token")).thenReturn(expectedResponse);

        var result = savingsController.calculateConsolidatedYield("Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(savingsService).calculateConsolidatedYield("Bearer token");
    }

    private SavingsResponse createSavingsResponse() {
        return new SavingsResponse(
                UUID.randomUUID(),
                "Reserva de Emergência",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Descrição teste",
                LocalDate.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
