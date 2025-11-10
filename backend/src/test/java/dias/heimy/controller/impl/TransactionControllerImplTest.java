package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.domain.enums.TransactionType;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.service.TransactionService;
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
@DisplayName("Tests for TransactionControllerImpl")
class TransactionControllerImplTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionControllerImpl transactionController;

    @Test
    @DisplayName("Should create transaction successfully when valid data provided")
    void shouldCreateTransaction_WhenValidData() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Salário", new BigDecimal("3000.00"), LocalDate.now(), "Salário mensal");
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(transactionService.createTransaction(any(TransactionRequest.class), anyString()))
                .thenReturn(expectedResponse);

        var result = transactionController.createTransaction(request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(transactionService).createTransaction(request, "Bearer token");
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void shouldGetTransactionById_WhenValidId() {

        var transactionId = UUID.randomUUID();
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(transactionService.getTransactionById(transactionId, "Bearer token"))
                .thenReturn(expectedResponse);

        var result = transactionController.getTransactionById(transactionId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(transactionService).getTransactionById(transactionId, "Bearer token");
    }

    @Test
    @DisplayName("Should list transactions successfully with pagination")
    void shouldListTransactions_WhenValidPagination() {

        var pageable = PageRequest.of(0, 10);
        var transactionResponse = createTransactionResponse(TransactionType.INCOME);
        var transactionsPage = new PageImpl<>(List.of(transactionResponse), pageable, 1);

        when(transactionService.listTransactions(any(Pageable.class), anyString()))
                .thenReturn(transactionsPage);

        var result = transactionController.listTransactions(0, 10, "date", "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().content()).hasSize(1);
        assertThat(result.getBody().pageNumber()).isZero();
        assertThat(result.getBody().pageSize()).isEqualTo(10);
        assertThat(result.getBody().elements()).isEqualTo(1);
        verify(transactionService).listTransactions(any(Pageable.class), eq("Bearer token"));
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void shouldUpdateTransaction_WhenValidData() {

        var transactionId = UUID.randomUUID();
        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Alimentação", new BigDecimal("150.00"), LocalDate.now(), "Supermercado");
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(transactionService.updateTransaction(transactionId, request, "Bearer token"))
                .thenReturn(expectedResponse);

        var result = transactionController.updateTransaction(transactionId, request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(transactionService).updateTransaction(transactionId, request, "Bearer token");
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransaction_WhenValidId() {

        var transactionId = UUID.randomUUID();

        doNothing().when(transactionService).deleteTransaction(transactionId, "Bearer token");

        var result = transactionController.deleteTransaction(transactionId, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(transactionService).deleteTransaction(transactionId, "Bearer token");
    }

    @Test
    @DisplayName("Should handle empty transactions list")
    void shouldHandleEmptyTransactionsList() {

        var pageable = PageRequest.of(0, 10);
        var emptyPage = new PageImpl<TransactionResponse>(List.of(), pageable, 0);

        when(transactionService.listTransactions(any(Pageable.class), anyString()))
                .thenReturn(emptyPage);

        var result = transactionController.listTransactions(0, 10, "date", "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().content()).isEmpty();
        assertThat(result.getBody().elements()).isZero();
        verify(transactionService).listTransactions(any(Pageable.class), eq("Bearer token"));
    }

    @Test
    @DisplayName("Should create income transaction successfully")
    void shouldCreateIncomeTransaction() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Freelance", new BigDecimal("1500.00"), LocalDate.now(), "Projeto freelance");
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(transactionService.createTransaction(request, "Bearer token")).thenReturn(expectedResponse);

        var result = transactionController.createTransaction(request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().type()).isEqualTo(TransactionType.INCOME);
    }

    @Test
    @DisplayName("Should create expense transaction successfully")
    void shouldCreateExpenseTransaction() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE,
                "Transporte",
                new BigDecimal("250.00"),
                LocalDate.now(),
                "Combustível e manutenção");
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(transactionService.createTransaction(request, "Bearer token")).thenReturn(expectedResponse);

        var result = transactionController.createTransaction(request, "Bearer token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().type()).isEqualTo(TransactionType.EXPENSE);
    }

    private TransactionResponse createTransactionResponse(TransactionType type) {
        return new TransactionResponse(
                UUID.randomUUID().toString(),
                type,
                "Categoria Teste",
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Descrição teste",
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
