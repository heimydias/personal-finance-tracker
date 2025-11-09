package dias.heimy.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para autenticação de usuário")
public record LoginRequest(
        @Schema(description = "Email do usuário", example = "admin@admin.com")
                @NotBlank(message = "Email é obrigatório")
                @Email(message = "Email deve ter formato válido")
                @JsonProperty("email")
                String email,
        @Schema(description = "Senha do usuário", example = "admin123")
                @NotBlank(message = "Senha é obrigatória")
                @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
                @JsonProperty("password")
                String password) {}
