package dias.heimy.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import dias.heimy.domain.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with error code and message")
    void shouldCreateException_WithErrorCodeAndMessage() {

        var errorCode = ErrorCode.INVALID_TOKEN;
        var message = "Test message";

        var exception = new BusinessException(errorCode, message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
        assertThat(exception.getHttpStatusCode()).isEqualTo(401);
        assertThat(exception.getHttpStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should create exception with error code, message and cause")
    void shouldCreateException_WithErrorCodeMessageAndCause() {

        var errorCode = ErrorCode.VALIDATION_ERROR;
        var message = "Test message";
        var cause = new RuntimeException("Root cause");

        var exception = new BusinessException(errorCode, message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatusCode()).isEqualTo(400);
        assertThat(exception.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    @DisplayName("Should determine correct http status based on error code")
    void shouldDetermineCorrectHttpStatus() {

        var authException = new BusinessException(ErrorCode.INVALID_TOKEN, "Auth error");
        assertThat(authException.getHttpStatus()).isEqualTo(UNAUTHORIZED);

        var businessException = new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Business error");
        assertThat(businessException.getHttpStatus()).isEqualTo(CONFLICT);

        var technicalException = new BusinessException(ErrorCode.VALIDATION_ERROR, "Technical error");
        assertThat(technicalException.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    @DisplayName("Should determine FORBIDDEN status for operation not permitted")
    void shouldDetermineForbiddenStatus_ForOperationNotPermitted() {

        var exception = new BusinessException(ErrorCode.OPERATION_NOT_PERMITTED, "Operation not permitted");

        assertThat(exception.getHttpStatus()).isEqualTo(FORBIDDEN);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine FORBIDDEN status for unauthorized access")
    void shouldDetermineForbiddenStatus_ForUnauthorizedAccess() {

        var exception = new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "Unauthorized");

        assertThat(exception.getHttpStatus()).isEqualTo(FORBIDDEN);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine NOT_FOUND status for transaction not found")
    void shouldDetermineNotFoundStatus_ForTransactionNotFound() {

        var exception = new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found");

        assertThat(exception.getHttpStatus()).isEqualTo(NOT_FOUND);
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should determine UNAUTHORIZED status for all auth errors")
    void shouldDetermineUnauthorizedStatus_ForAllAuthErrors() {

        var invalidToken = new BusinessException(ErrorCode.INVALID_TOKEN, "Invalid token");
        var tokenExpired = new BusinessException(ErrorCode.TOKEN_EXPIRED, "Token expired");
        var invalidCredentials = new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        var adminAuthRequired = new BusinessException(ErrorCode.ADMIN_AUTH_REQUIRED, "Admin auth required");

        assertThat(invalidToken.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(tokenExpired.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(invalidCredentials.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(adminAuthRequired.getHttpStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should get error code from ErrorCode enum")
    void shouldGetErrorCodeFromErrorCodeEnum() {

        var exception = new BusinessException(ErrorCode.USER_NOT_FOUND, "User not found");

        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var exception = new BusinessException(ErrorCode.VALIDATION_ERROR, "Test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new BusinessException(ErrorCode.VALIDATION_ERROR, "Test");

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }
}
