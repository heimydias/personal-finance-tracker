package dias.heimy.dto.request;

import dias.heimy.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request para criar/atualizar transação")
public record TransactionRequest(
        @Schema(
                        description = "Tipo da transação",
                        example = "INCOME",
                        allowableValues = {"INCOME", "EXPENSE"})
                @NotNull(message = "Tipo é obrigatório")
                TransactionType type,
        @Schema(description = "Categoria da transação", example = "Salário")
                @NotBlank(message = "Categoria é obrigatória")
                String category,
        @Schema(description = "Valor da transação", example = "1500.00")
                @NotNull(message = "Valor é obrigatório")
                @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
                BigDecimal amount,
        @Schema(description = "Data da transação", example = "2025-10-15") @NotNull(message = "Data é obrigatória")
                LocalDate transactionDate,
        @Schema(description = "Descrição da transação", example = "Salário mensal") String description) {}
