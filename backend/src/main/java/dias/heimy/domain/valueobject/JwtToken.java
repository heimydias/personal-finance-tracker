package dias.heimy.domain.valueobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record JwtToken(@NotBlank String token, @NotNull Instant issuedAt, @NotNull Instant expiresAt) {

    public JwtToken {
        if (issuedAt != null && expiresAt != null && issuedAt.isAfter(expiresAt)) {
            throw new IllegalArgumentException("IssuedAt cannot be after ExpiresAt");
        }
    }
}
