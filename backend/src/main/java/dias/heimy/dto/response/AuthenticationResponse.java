package dias.heimy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com tokens JWT")
public record AuthenticationResponse(
        @Schema(description = "Token JWT de acesso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                @JsonProperty("accessToken")
                String accessToken,
        @Schema(description = "Token JWT de refresh", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                @JsonProperty("refreshToken")
                String refreshToken,
        @Schema(description = "Tipo do token", example = "Bearer") @JsonProperty("tokenType") String tokenType,
        @Schema(description = "Tempo de expiração em segundos", example = "3600") @JsonProperty("expiresIn")
                Long expiresIn,
        @Schema(description = "ID do usuário autenticado") @JsonProperty("userId") String userId,
        @Schema(description = "Nome do usuário autenticado", example = "João Silva") @JsonProperty("userName")
                String userName,
        @Schema(description = "Email do usuário autenticado", example = "user@example.com") @JsonProperty("userEmail")
                String userEmail,
        @Schema(description = "Role do usuário autenticado", example = "USER") @JsonProperty("userRole")
                String userRole) {
    public static AuthenticationResponse of(
            String accessToken,
            String refreshToken,
            Long expiresIn,
            String userId,
            String userName,
            String userEmail,
            String userRole) {
        return new AuthenticationResponse(
                accessToken, refreshToken, "Bearer", expiresIn, userId, userName, userEmail, userRole);
    }
}
