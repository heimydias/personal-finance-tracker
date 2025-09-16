package dias.heimy.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dias.heimy.domain.valueobject.JwtToken;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for JwtToken")
class JwtTokenTest {

    @Test
    @DisplayName("Should create valid JWT token")
    void shouldCreateValidJwtToken() {
        String tokenValue = "valid.jwt.token";
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(3600);

        JwtToken token = new JwtToken(tokenValue, issuedAt, expiresAt);

        assertThat(token.token()).isEqualTo(tokenValue);
        assertThat(token.issuedAt()).isEqualTo(issuedAt);
        assertThat(token.expiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when issuedAt is after expiresAt")
    void shouldThrowException_WhenIssuedAtIsAfterExpiresAt() {
        String tokenValue = "valid.jwt.token";
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.minusSeconds(3600);

        assertThatThrownBy(() -> new JwtToken(tokenValue, issuedAt, expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IssuedAt cannot be after ExpiresAt");
    }

    @Test
    @DisplayName("Should create JWT token when issuedAt equals expiresAt")
    void shouldCreateJwtToken_WhenIssuedAtEqualsExpiresAt() {
        String tokenValue = "valid.jwt.token";
        Instant instant = Instant.now();

        JwtToken token = new JwtToken(tokenValue, instant, instant);

        assertThat(token.token()).isEqualTo(tokenValue);
        assertThat(token.issuedAt()).isEqualTo(instant);
        assertThat(token.expiresAt()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should create JWT token with null issuedAt")
    void shouldCreateJwtToken_WithNullIssuedAt() {
        String tokenValue = "valid.jwt.token";
        Instant expiresAt = Instant.now().plusSeconds(3600);

        JwtToken token = new JwtToken(tokenValue, null, expiresAt);

        assertThat(token.token()).isEqualTo(tokenValue);
        assertThat(token.issuedAt()).isNull();
        assertThat(token.expiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Should create JWT token with null expiresAt")
    void shouldCreateJwtToken_WithNullExpiresAt() {
        String tokenValue = "valid.jwt.token";
        Instant issuedAt = Instant.now();

        JwtToken token = new JwtToken(tokenValue, issuedAt, null);

        assertThat(token.token()).isEqualTo(tokenValue);
        assertThat(token.issuedAt()).isEqualTo(issuedAt);
        assertThat(token.expiresAt()).isNull();
    }

    @Test
    @DisplayName("Should create JWT token with both null timestamps")
    void shouldCreateJwtToken_WithBothNullTimestamps() {
        String tokenValue = "valid.jwt.token";

        JwtToken token = new JwtToken(tokenValue, null, null);

        assertThat(token.token()).isEqualTo(tokenValue);
        assertThat(token.issuedAt()).isNull();
        assertThat(token.expiresAt()).isNull();
    }
}
