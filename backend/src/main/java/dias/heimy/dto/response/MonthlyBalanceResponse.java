package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response com saldo mensal")
public record MonthlyBalanceResponse(
        @Schema(description = "Ano") @JsonProperty("year") int year,
        @Schema(description = "Mês") @JsonProperty("month") int month,
        @Schema(description = "Total de receitas") @JsonProperty("totalIncome") BigDecimal totalIncome,
        @Schema(description = "Total de despesas") @JsonProperty("totalExpense") BigDecimal totalExpense,
        @Schema(description = "Saldo (receitas - despesas)") @JsonProperty("balance") BigDecimal balance) {

    public static MonthlyBalanceResponse of(int year, int month, BigDecimal totalIncome, BigDecimal totalExpense) {
        BigDecimal balance = totalIncome.subtract(totalExpense);
        return new MonthlyBalanceResponse(year, month, totalIncome, totalExpense, balance);
    }
}
