package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response com saldo mensal")
public record MonthlyBalanceResponse(
        @Schema(description = "Ano") @JsonProperty("year") int year,
        @Schema(description = "Mês") @JsonProperty("month") int month,
        @Schema(description = "Total de receitas do mês") @JsonProperty("totalIncome") BigDecimal totalIncome,
        @Schema(description = "Total de despesas do mês") @JsonProperty("totalExpense") BigDecimal totalExpense,
        @Schema(description = "Depósitos líquidos em poupança do mês (depósitos - resgates)")
                @JsonProperty("monthlySavingsDeposit")
                BigDecimal monthlySavingsDeposit,
        @Schema(description = "Total de depósitos em poupança do mês") @JsonProperty("monthlySavingsDepositsTotal")
                BigDecimal monthlySavingsDepositsTotal,
        @Schema(description = "Total de resgates de poupança do mês") @JsonProperty("monthlySavingsWithdrawalsTotal")
                BigDecimal monthlySavingsWithdrawalsTotal,
        @Schema(description = "Saldo em poupança apenas do mês (depósitos - resgates do mês)")
                @JsonProperty("monthlySavingsBalance")
                BigDecimal monthlySavingsBalance,
        @Schema(description = "Saldo em conta corrente acumulado até o mês") @JsonProperty("accountBalance")
                BigDecimal accountBalance,
        @Schema(description = "Saldo em poupança acumulado até o mês") @JsonProperty("savingsBalance")
                BigDecimal savingsBalance,
        @Schema(description = "Saldo total acumulado (conta + poupança)") @JsonProperty("totalBalance")
                BigDecimal totalBalance,
        @Schema(description = "Saldo disponível do período (receitas - despesas - poupança do mês)")
                @JsonProperty("balance")
                BigDecimal balance) {

    public static MonthlyBalanceResponse of(
            int year,
            int month,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal monthlySavingsDeposit,
            BigDecimal monthlySavingsDepositsTotal,
            BigDecimal monthlySavingsWithdrawalsTotal,
            BigDecimal monthlySavingsBalance,
            BigDecimal accountBalance,
            BigDecimal savingsBalance) {
        BigDecimal totalBalance = accountBalance.add(savingsBalance);
        BigDecimal balance = totalIncome.subtract(totalExpense).subtract(monthlySavingsDeposit);
        return new MonthlyBalanceResponse(
                year,
                month,
                totalIncome,
                totalExpense,
                monthlySavingsDeposit,
                monthlySavingsDepositsTotal,
                monthlySavingsWithdrawalsTotal,
                monthlySavingsBalance,
                accountBalance,
                savingsBalance,
                totalBalance,
                balance);
    }
}
