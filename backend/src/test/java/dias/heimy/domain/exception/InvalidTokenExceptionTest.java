package dias.heimy.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Tests for InvalidTokenException")
class InvalidTokenExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateException_WithMessage() {

        var message = "Token inválido";

        var exception = new InvalidTokenException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateException_WithMessageAndCause() {

        var message = "Token inválido";
        var cause = new RuntimeException("Root cause");

        var exception = new InvalidTokenException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Root cause");
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOfBusinessException() {

        var exception = new InvalidTokenException("Test");

        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new InvalidTokenException("Test");

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }
}
