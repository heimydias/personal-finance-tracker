package dias.heimy.domain.valueobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record RefreshToken(@NotBlank String token, @NotNull Instant expiresAt) {}
