package dias.heimy.dto.request;

import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para registro de novo usuário")
public record UserRegisterRequest(
        @Schema(description = "Nome do usuário", example = "João Silva") @NotBlank(message = "Nome é obrigatório")
                String name,
        @Schema(description = "Email do usuário", example = "newuser@example.com")
                @NotBlank(message = "Email é obrigatório")
                @Email(message = "Email deve ter formato válido")
                String email,
        @Schema(description = "Senha do usuário", example = "Password123!")
                @NotBlank(message = "Senha é obrigatória")
                @ValidPassword(
                        minLength = 8,
                        requireUppercase = true,
                        requireLowercase = true,
                        requireDigit = true,
                        requireSpecialChar = true)
                String password,
        @Schema(
                        description = "Perfil do usuário",
                        example = "USER",
                        allowableValues = {"ADMIN", "USER"})
                @NotNull(message = "Role é obrigatório")
                UserRole role) {}
