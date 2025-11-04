package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response com saldo consolidado do usuário")
public record UserBalanceResponse(
        @Schema(description = "ID do saldo") @JsonProperty("id") UUID id,
        @Schema(description = "ID do usuário") @JsonProperty("userId") UUID userId,
        @Schema(description = "Total de receitas") @JsonProperty("totalIncome") BigDecimal totalIncome,
        @Schema(description = "Total de despesas") @JsonProperty("totalExpense") BigDecimal totalExpense,
        @Schema(description = "Saldo em conta corrente") @JsonProperty("accountBalance") BigDecimal accountBalance,
        @Schema(description = "Saldo em poupança") @JsonProperty("savingsBalance") BigDecimal savingsBalance,
        @Schema(description = "Saldo total (conta + poupança)") @JsonProperty("totalBalance") BigDecimal totalBalance,
        @Schema(description = "Data de criação") @JsonProperty("createdAt") LocalDateTime createdAt,
        @Schema(description = "Data da última atualização") @JsonProperty("updatedAt") LocalDateTime updatedAt) {}
