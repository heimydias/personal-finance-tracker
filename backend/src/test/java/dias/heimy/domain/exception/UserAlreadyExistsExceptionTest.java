package dias.heimy.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Tests for UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create exception with email message")
    void shouldCreateException_WithEmailMessage() {

        var email = "test@example.com";

        var exception = new UserAlreadyExistsException(email);

        assertThat(exception.getMessage()).contains(email);
        assertThat(exception.getErrorCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(exception.getHttpStatusCode()).isEqualTo(409);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should have correct error properties")
    void shouldHaveCorrectErrorProperties() {
        var exception = new UserAlreadyExistsException("user@test.com");

        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getErrorCode()).isNotNull();
        assertThat(exception.getHttpStatusCode()).isPositive();
        assertThat(exception.getHttpStatus()).isNotNull();
    }
}
