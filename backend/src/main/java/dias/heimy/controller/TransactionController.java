package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.TRANSACTIONS;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Transactions", description = "Endpoints para gerenciamento de receitas e despesas")
@RequestMapping(value = TRANSACTIONS, produces = APPLICATION_JSON_VALUE)
public interface TransactionController {

    @Operation(
            summary = "Criar nova transação",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PostMapping
    ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Buscar transação por ID",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/{id}")
    ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Listar transações do usuário",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    ResponseEntity<PageResponse<TransactionResponse>> listTransactions(
            @Parameter(description = "Número da página (0-indexed)", example = "0") @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação (ex: id, transactionDate, amount)", example = "transactionDate")
                    @RequestParam(defaultValue = "transactionDate")
                    String sort,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Atualizar transação",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping("/{id}")
    ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Deletar transação",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTransaction(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);
}
