package dias.heimy.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ConsolidatedYieldResponse(
        int totalSavings,
        BigDecimal totalAmount,
        BigDecimal averageInterestRate,
        BigDecimal totalMonthlyYield,
        BigDecimal totalAfterYield,
        List<MonthlyYieldResponse> savingsYields) {

    public static ConsolidatedYieldResponse of(
            int totalSavings,
            BigDecimal totalAmount,
            BigDecimal averageInterestRate,
            BigDecimal totalMonthlyYield,
            BigDecimal totalAfterYield,
            List<MonthlyYieldResponse> savingsYields) {
        return new ConsolidatedYieldResponse(
                totalSavings, totalAmount, averageInterestRate, totalMonthlyYield, totalAfterYield, savingsYields);
    }
}
