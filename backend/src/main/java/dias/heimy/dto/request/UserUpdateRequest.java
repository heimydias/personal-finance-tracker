package dias.heimy.dto.request;

import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Request para atualização de usuário")
public record UserUpdateRequest(
        @Schema(description = "Email do usuário", example = "user@example.com")
                @Email(message = "Email deve ter formato válido")
                String email,
        @Schema(description = "Nova senha do usuário (opcional)")
                @ValidPassword(
                        minLength = 8,
                        requireUppercase = true,
                        requireLowercase = true,
                        requireDigit = true,
                        requireSpecialChar = true)
                String password,
        @Schema(
                        description = "Perfil do usuário (apenas admin pode alterar)",
                        example = "USER",
                        allowableValues = {"ADMIN", "USER"})
                UserRole role) {}
