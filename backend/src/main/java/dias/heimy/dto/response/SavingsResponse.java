package dias.heimy.dto.response;

import dias.heimy.domain.enums.SavingsType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resposta contendo informações de uma poupança")
public record SavingsResponse(
        @Schema(description = "ID da poupança", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
        @Schema(description = "Nome da poupança", example = "Minha Reserva") String name,
        @Schema(description = "Tipo da poupança", example = "EMERGENCY_FUND") SavingsType type,
        @Schema(description = "Valor atual da poupança", example = "5000.00") BigDecimal amount,
        @Schema(description = "Taxa de rendimento mensal em porcentagem", example = "0.50") BigDecimal interestRate,
        @Schema(description = "Descrição da poupança", example = "Poupança para emergências") String description,
        @Schema(description = "Data da transação", example = "2025-10-19") LocalDate transactionDate,
        @Schema(description = "ID do usuário proprietário", example = "123e4567-e89b-12d3-a456-426614174001")
                UUID userId,
        @Schema(description = "ID da transação de depósito", example = "123e4567-e89b-12d3-a456-426614174002")
                UUID depositTransactionId,
        @Schema(description = "Data de criação", example = "2025-01-15T10:30:00") LocalDateTime createdAt,
        @Schema(description = "Data da última atualização", example = "2025-01-15T10:30:00") LocalDateTime updatedAt) {}
