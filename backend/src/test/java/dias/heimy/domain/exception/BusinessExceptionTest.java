package dias.heimy.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import dias.heimy.domain.enums.ExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create custom business exception with message")
    void shouldCreateCustomBusinessException_WithMessage() {

        var exception = new CustomBusinessException("Test message");

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getErrorCode()).isEqualTo("CUSTOM_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
        assertThat(exception.getType()).isEqualTo(ExceptionType.VALIDATION);
    }

    @Test
    @DisplayName("Should create custom business exception with message and cause")
    void shouldCreateCustomBusinessException_WithMessageAndCause() {

        var cause = new RuntimeException("Root cause");
        var exception = new CustomBusinessException("Test message", cause);

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Root cause");
        assertThat(exception.getErrorCode()).isEqualTo("CUSTOM_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return custom HTTP status code")
    void shouldReturnCustomHttpStatusCode() {

        var exception = new CustomBusinessExceptionWithStatus("Test message");

        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var exception = new CustomBusinessException("Test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new CustomBusinessException("Test");

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    static class CustomBusinessException extends BusinessException {

        public CustomBusinessException(String message) {
            super(message);
        }

        public CustomBusinessException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public String getErrorCode() {
            return "CUSTOM_ERROR";
        }

        @Override
        public ExceptionType getType() {
            return ExceptionType.VALIDATION;
        }
    }

    static class CustomBusinessExceptionWithStatus extends BusinessException {

        public CustomBusinessExceptionWithStatus(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return "CUSTOM_ERROR";
        }

        @Override
        public int getHttpStatusCode() {
            return 403;
        }

        @Override
        public ExceptionType getType() {
            return ExceptionType.AUTHORIZATION;
        }
    }
}
