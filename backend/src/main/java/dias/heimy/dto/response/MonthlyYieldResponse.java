package dias.heimy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Resposta contendo o cálculo de rendimento mensal da poupança")
public record MonthlyYieldResponse(
        @Schema(description = "ID da poupança", example = "123e4567-e89b-12d3-a456-426614174000") UUID savingsId,
        @Schema(description = "Nome da poupança", example = "Reserva de Emergência") String savingsName,
        @Schema(description = "Valor atual da poupança", example = "5000.00") BigDecimal currentAmount,
        @Schema(description = "Taxa de rendimento mensal em porcentagem", example = "0.50") BigDecimal interestRate,
        @Schema(description = "Rendimento esperado no mês", example = "25.00") BigDecimal monthlyYield,
        @Schema(description = "Valor total após rendimento", example = "5025.00") BigDecimal totalAfterYield) {}
