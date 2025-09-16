package dias.heimy.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-access-secret-key-for-testing-purposes-must-be-long-enough",
                "test-refresh-secret-key-for-testing-purposes-must-be-long-enough",
                15,
                7);
    }

    @Test
    @DisplayName("Should generate valid access token")
    void shouldGenerateValidAccessToken() {
        String email = "test@example.com";
        String role = "ROLE_USER";

        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        assertThat(token).isNotNull();
        assertThat(token.token()).isNotBlank();
        assertThat(token.issuedAt()).isBefore(Instant.now().plusSeconds(1));
        assertThat(token.expiresAt()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void shouldGenerateValidRefreshToken() {
        String email = "test@example.com";

        RefreshToken token = jwtTokenProvider.generateRefreshToken(email);

        assertThat(token).isNotNull();
        assertThat(token.token()).isNotBlank();
        assertThat(token.expiresAt()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Should extract email from valid access token")
    void shouldExtractEmailFromValidAccessToken() {
        String email = "test@example.com";
        String role = "ROLE_USER";
        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        String extractedEmail = jwtTokenProvider.extractEmailFromToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Should extract email from valid refresh token")
    void shouldExtractEmailFromValidRefreshToken() {
        String email = "test@example.com";
        RefreshToken token = jwtTokenProvider.generateRefreshToken(email);

        String extractedEmail = jwtTokenProvider.extractEmailFromRefreshToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Should validate valid access token")
    void shouldValidateValidAccessToken() {
        String email = "test@example.com";
        String role = "ROLE_USER";
        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        boolean isValid = jwtTokenProvider.validateToken(token.token());

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should validate valid refresh token")
    void shouldValidateValidRefreshToken() {
        String email = "test@example.com";
        RefreshToken token = jwtTokenProvider.generateRefreshToken(email);

        boolean isValid = jwtTokenProvider.validateRefreshToken(token.token());

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when access token is invalid")
    void shouldThrowInvalidTokenException_WhenAccessTokenIsInvalid() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid access token");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is invalid")
    void shouldThrowInvalidTokenException_WhenRefreshTokenIsInvalid() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should return false for invalid access token validation")
    void shouldReturnFalse_ForInvalidAccessTokenValidation() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false for invalid refresh token validation")
    void shouldReturnFalse_ForInvalidRefreshTokenValidation() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when access token is malformed")
    void shouldThrowInvalidTokenException_WhenAccessTokenIsMalformed() {
        String malformedToken = "malformed";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromToken(malformedToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid access token");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is malformed")
    void shouldThrowInvalidTokenException_WhenRefreshTokenIsMalformed() {
        String malformedToken = "malformed";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(malformedToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }
}
