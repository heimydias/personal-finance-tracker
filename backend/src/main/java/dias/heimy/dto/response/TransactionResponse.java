package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dias.heimy.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response com dados da transação")
public record TransactionResponse(
        @Schema(description = "ID da transação") @JsonProperty("id") String id,
        @Schema(description = "Tipo da transação") @JsonProperty("type") TransactionType type,
        @Schema(description = "Categoria") @JsonProperty("category") String category,
        @Schema(description = "Valor") @JsonProperty("amount") BigDecimal amount,
        @Schema(description = "Data da transação") @JsonProperty("transactionDate") LocalDate transactionDate,
        @Schema(description = "Descrição") @JsonProperty("description") String description,
        @Schema(description = "ID do usuário") @JsonProperty("userId") String userId,
        @Schema(description = "Data de criação") @JsonProperty("createdAt") LocalDateTime createdAt,
        @Schema(description = "Data de atualização") @JsonProperty("updatedAt") LocalDateTime updatedAt) {}
