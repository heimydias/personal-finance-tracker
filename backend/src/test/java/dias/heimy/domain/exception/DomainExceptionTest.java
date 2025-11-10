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
        assertThat(exception.getHttpStatus()).isEqualTo(UNAUTHORIZED);
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
        assertThat(exception.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    @DisplayName("Should determine correct http status based on error code")
    void shouldDetermineCorrectHttpStatus() {

        var authException = new DomainException(ErrorCode.INVALID_TOKEN, "Auth error");
        assertThat(authException.getHttpStatus()).isEqualTo(UNAUTHORIZED);

        var businessException = new DomainException(ErrorCode.USER_ALREADY_EXISTS, "Business error");
        assertThat(businessException.getHttpStatus()).isEqualTo(CONFLICT);

        var technicalException = new DomainException(ErrorCode.VALIDATION_ERROR, "Technical error");
        assertThat(technicalException.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    @DisplayName("Should determine FORBIDDEN status for operation not permitted")
    void shouldDetermineForbiddenStatus_ForOperationNotPermitted() {

        var exception = new DomainException(ErrorCode.OPERATION_NOT_PERMITTED, "Operation not permitted");

        assertThat(exception.getHttpStatus()).isEqualTo(FORBIDDEN);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine FORBIDDEN status for unauthorized access")
    void shouldDetermineForbiddenStatus_ForUnauthorizedAccess() {

        var exception = new DomainException(ErrorCode.UNAUTHORIZED_ACCESS, "Unauthorized");

        assertThat(exception.getHttpStatus()).isEqualTo(FORBIDDEN);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine NOT_FOUND status for transaction not found")
    void shouldDetermineNotFoundStatus_ForTransactionNotFound() {

        var exception = new DomainException(ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found");

        assertThat(exception.getHttpStatus()).isEqualTo(NOT_FOUND);
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should determine UNAUTHORIZED status for all auth errors")
    void shouldDetermineUnauthorizedStatus_ForAllAuthErrors() {

        var invalidToken = new DomainException(ErrorCode.INVALID_TOKEN, "Invalid token");
        var tokenExpired = new DomainException(ErrorCode.TOKEN_EXPIRED, "Token expired");
        var invalidCredentials = new DomainException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        var adminAuthRequired = new DomainException(ErrorCode.ADMIN_AUTH_REQUIRED, "Admin auth required");

        assertThat(invalidToken.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(tokenExpired.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(invalidCredentials.getHttpStatus()).isEqualTo(UNAUTHORIZED);
        assertThat(adminAuthRequired.getHttpStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should get error code from ErrorCode enum")
    void shouldGetErrorCodeFromErrorCodeEnum() {

        var exception = new DomainException(ErrorCode.USER_NOT_FOUND, "User not found");

        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOfBusinessException() {

        var exception = new DomainException(ErrorCode.VALIDATION_ERROR, "Test");

        assertThat(exception).isInstanceOf(BusinessException.class);
    }
}
