package dias.heimy.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para refresh do token JWT")
public record RefreshTokenRequest(
        @Schema(description = "Token de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                @NotBlank(message = "Refresh token é obrigatório")
                @JsonProperty("refreshToken")
                String refreshToken) {}
