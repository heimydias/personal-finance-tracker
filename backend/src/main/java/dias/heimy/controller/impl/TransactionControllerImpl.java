package dias.heimy.controller.impl;

import dias.heimy.controller.TransactionController;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.service.TransactionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(
            TransactionRequest request, String authorizationHeader) {
        log.info("Requisição para criar transação: {} - R$ {}", request.type(), request.amount());
        TransactionResponse response = transactionService.createTransaction(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TransactionResponse> getTransactionById(UUID id, String authorizationHeader) {
        log.info("Requisição para buscar transação: {}", id);
        TransactionResponse response = transactionService.getTransactionById(id, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PageResponse<TransactionResponse>> listTransactions(
            Pageable pageable, String authorizationHeader) {
        log.info(
                "Requisição para listar transações - page: {}, size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        Page<TransactionResponse> page = transactionService.listTransactions(pageable, authorizationHeader);
        PageResponse<TransactionResponse> response =
                PageResponse.of(page.getNumber(), page.getSize(), page.getTotalElements(), page.getContent());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TransactionResponse> updateTransaction(
            UUID id, TransactionRequest request, String authorizationHeader) {
        log.info("Requisição para atualizar transação: {}", id);
        TransactionResponse response = transactionService.updateTransaction(id, request, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(UUID id, String authorizationHeader) {
        log.info("Requisição para deletar transação: {}", id);
        transactionService.deleteTransaction(id, authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MonthlyBalanceResponse> getMonthlyBalance(int year, int month, String authorizationHeader) {
        log.info("Requisição para calcular saldo mensal: {}/{}", year, month);
        MonthlyBalanceResponse response = transactionService.getMonthlyBalance(year, month, authorizationHeader);
        return ResponseEntity.ok(response);
    }
}
