package dias.heimy.dto.request;

import dias.heimy.domain.enums.SavingsType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request para criar ou atualizar uma poupança")
public record SavingsRequest(
        @NotBlank(message = "Nome da poupança é obrigatório")
                @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
                @Schema(description = "Nome da poupança", example = "Minha Reserva")
                String name,
        @NotNull(message = "Tipo da poupança é obrigatório")
                @Schema(
                        description =
                                "Tipo da poupança. Valores: EMERGENCY_FUND (Reserva de Emergência), RETIREMENT (Aposentadoria), TRAVEL (Viagem), INVESTMENT (Investimento), EDUCATION (Educação), HOME (Casa/Imóvel), VEHICLE (Veículo), OTHER (Outros)",
                        example = "EMERGENCY_FUND",
                        allowableValues = {
                            "EMERGENCY_FUND",
                            "RETIREMENT",
                            "TRAVEL",
                            "INVESTMENT",
                            "EDUCATION",
                            "HOME",
                            "VEHICLE",
                            "OTHER"
                        })
                SavingsType type,
        @NotNull(message = "Valor da poupança é obrigatório")
                @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
                @Schema(description = "Valor inicial da poupança", example = "5000.00")
                BigDecimal amount,
        @NotNull(message = "Taxa de rendimento é obrigatória")
                @DecimalMin(value = "0.00", message = "Taxa de rendimento deve ser positiva")
                @Schema(description = "Taxa de rendimento mensal em porcentagem", example = "0.50")
                BigDecimal interestRate,
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
                @Schema(description = "Descrição da poupança", example = "Poupança para emergências")
                String description,
        @NotNull(message = "Data da transação é obrigatória")
                @Schema(description = "Data da transação", example = "2025-10-19")
                LocalDate transactionDate) {}
