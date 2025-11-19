package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Previsão financeira para o próximo mês")
public record ForecastResponse(
        @Schema(description = "Ano alvo da previsão") @JsonProperty("targetYear") int targetYear,
        @Schema(description = "Mês alvo da previsão (1-12)") @JsonProperty("targetMonth") int targetMonth,
        @Schema(description = "Quantidade de meses analisados") @JsonProperty("monthsAnalyzed") int monthsAnalyzed,
        @Schema(description = "Média de receitas dos meses analisados") @JsonProperty("averageIncome")
                BigDecimal averageIncome,
        @Schema(description = "Média de despesas dos meses analisados") @JsonProperty("averageExpense")
                BigDecimal averageExpense,
        @Schema(description = "Média de depósitos em poupança dos meses analisados")
                @JsonProperty("averageSavingsDeposit")
                BigDecimal averageSavingsDeposit,
        @Schema(description = "Receita projetada para o próximo mês") @JsonProperty("projectedIncome")
                BigDecimal projectedIncome,
        @Schema(description = "Despesa projetada para o próximo mês") @JsonProperty("projectedExpense")
                BigDecimal projectedExpense,
        @Schema(description = "Rendimento projetado das poupanças") @JsonProperty("projectedSavingsYield")
                BigDecimal projectedSavingsYield,
        @Schema(description = "Saldo em conta projetado") @JsonProperty("projectedAccountBalance")
                BigDecimal projectedAccountBalance,
        @Schema(description = "Saldo em poupança projetado") @JsonProperty("projectedSavingsBalance")
                BigDecimal projectedSavingsBalance,
        @Schema(description = "Saldo total projetado (conta + poupança)") @JsonProperty("projectedTotalBalance")
                BigDecimal projectedTotalBalance,
        @Schema(description = "Saldo atual em conta") @JsonProperty("currentAccountBalance")
                BigDecimal currentAccountBalance,
        @Schema(description = "Saldo atual em poupança") @JsonProperty("currentSavingsBalance")
                BigDecimal currentSavingsBalance,
        @Schema(description = "Saldo total atual") @JsonProperty("currentTotalBalance")
                BigDecimal currentTotalBalance,
        @Schema(description = "Histórico dos meses analisados") @JsonProperty("history")
                List<MonthlyHistoryResponse> history) {}
