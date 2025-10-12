package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ExceptionType.AUTHENTICATION;
import static dias.heimy.domain.enums.ExceptionType.CONFLICT;
import static dias.heimy.domain.enums.ExceptionType.VALIDATION;
import static org.assertj.core.api.Assertions.assertThat;

import dias.heimy.domain.enums.ErrorCode;
import dias.heimy.domain.enums.ExceptionType;
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

    @Test
    @DisplayName("Should determine AUTHORIZATION type for operation not permitted")
    void shouldDetermineAuthorizationType_ForOperationNotPermitted() {

        var exception = new DomainException(ErrorCode.OPERATION_NOT_PERMITTED, "Operation not permitted");

        assertThat(exception.getType()).isEqualTo(ExceptionType.AUTHORIZATION);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine AUTHORIZATION type for unauthorized access")
    void shouldDetermineAuthorizationType_ForUnauthorizedAccess() {

        var exception = new DomainException(ErrorCode.UNAUTHORIZED_ACCESS, "Unauthorized");

        assertThat(exception.getType()).isEqualTo(ExceptionType.AUTHORIZATION);
        assertThat(exception.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should determine CONFLICT type for transaction not found")
    void shouldDetermineConflictType_ForTransactionNotFound() {

        var exception = new DomainException(ErrorCode.TRANSACTION_NOT_FOUND, "Transaction not found");

        assertThat(exception.getType()).isEqualTo(CONFLICT);
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should determine AUTHENTICATION type for all auth errors")
    void shouldDetermineAuthenticationType_ForAllAuthErrors() {

        var invalidToken = new DomainException(ErrorCode.INVALID_TOKEN, "Invalid token");
        var tokenExpired = new DomainException(ErrorCode.TOKEN_EXPIRED, "Token expired");
        var invalidCredentials = new DomainException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        var adminAuthRequired = new DomainException(ErrorCode.ADMIN_AUTH_REQUIRED, "Admin auth required");
        var adminAuthInsufficient = new DomainException(ErrorCode.ADMIN_AUTH_INSUFFICIENT, "Admin auth insufficient");

        assertThat(invalidToken.getType()).isEqualTo(AUTHENTICATION);
        assertThat(tokenExpired.getType()).isEqualTo(AUTHENTICATION);
        assertThat(invalidCredentials.getType()).isEqualTo(AUTHENTICATION);
        assertThat(adminAuthRequired.getType()).isEqualTo(AUTHENTICATION);
        assertThat(adminAuthInsufficient.getType()).isEqualTo(AUTHENTICATION);
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
