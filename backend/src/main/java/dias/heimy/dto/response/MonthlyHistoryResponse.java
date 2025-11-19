package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Histórico financeiro de um mês específico")
public record MonthlyHistoryResponse(
        @Schema(description = "Ano") @JsonProperty("year") int year,
        @Schema(description = "Mês (1-12)") @JsonProperty("month") int month,
        @Schema(description = "Total de receitas do mês") @JsonProperty("income") BigDecimal income,
        @Schema(description = "Total de despesas do mês") @JsonProperty("expense") BigDecimal expense,
        @Schema(description = "Total depositado em poupança no mês") @JsonProperty("savingsDeposit")
                BigDecimal savingsDeposit,
        @Schema(description = "Saldo do mês (receitas - despesas)") @JsonProperty("balance") BigDecimal balance) {

    public static MonthlyHistoryResponse of(
            int year, int month, BigDecimal income, BigDecimal expense, BigDecimal savingsDeposit) {
        BigDecimal balance = income.subtract(expense);
        return new MonthlyHistoryResponse(year, month, income, expense, savingsDeposit, balance);
    }
}
