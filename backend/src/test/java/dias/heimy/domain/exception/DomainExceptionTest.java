package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ExceptionType.AUTHENTICATION;
import static dias.heimy.domain.enums.ExceptionType.CONFLICT;
import static dias.heimy.domain.enums.ExceptionType.VALIDATION;
import static org.assertj.core.api.Assertions.assertThat;

import dias.heimy.domain.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for DomainException")
class DomainExceptionTest {

    @Test
    @DisplayName("Should create exception with error code and message")
    void shouldCreateException_WithErrorCodeAndMessage() {

        var errorCode = ErrorCode.INVALID_TOKEN;
        var message = "Test message";

        var exception = new DomainException(errorCode, message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
        assertThat(exception.getType()).isEqualTo(AUTHENTICATION);
    }

    @Test
    @DisplayName("Should create exception with error code, message and cause")
    void shouldCreateException_WithErrorCodeMessageAndCause() {

        var errorCode = ErrorCode.VALIDATION_ERROR;
        var message = "Test message";
        var cause = new RuntimeException("Root cause");

        var exception = new DomainException(errorCode, message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
        assertThat(exception.getType()).isEqualTo(VALIDATION);
    }

    @Test
    @DisplayName("Should determine correct exception type based on error code")
    void shouldDetermineCorrectExceptionType() {

        var authException = new DomainException(ErrorCode.INVALID_TOKEN, "Auth error");
        assertThat(authException.getType()).isEqualTo(AUTHENTICATION);

        var businessException = new DomainException(ErrorCode.USER_ALREADY_EXISTS, "Business error");
        assertThat(businessException.getType()).isEqualTo(CONFLICT);

        var technicalException = new DomainException(ErrorCode.VALIDATION_ERROR, "Technical error");
        assertThat(technicalException.getType()).isEqualTo(VALIDATION);
    }
}
