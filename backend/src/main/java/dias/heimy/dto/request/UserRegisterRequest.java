package dias.heimy.dto.request;

import dias.heimy.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para registro de novo usuário")
public record UserRegisterRequest(
        @Schema(description = "Email do usuário", example = "newuser@example.com")
                @NotBlank(message = "Email é obrigatório")
                @Email(message = "Email deve ter formato válido")
                String email,
        @Schema(description = "Senha do usuário", example = "password123")
                @NotBlank(message = "Senha é obrigatória")
                @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
                String password,
        @Schema(
                        description = "Perfil do usuário",
                        example = "USER",
                        allowableValues = {"ADMIN", "USER"})
                @NotNull(message = "Role é obrigatório")
                UserRole role) {}
