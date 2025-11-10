package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for ErrorCode")
class ErrorCodeTest {

    @Test
    @DisplayName("Should have correct code and status for USER_ALREADY_EXISTS")
    void shouldHaveCorrectCodeAndStatus_ForUserAlreadyExists() {

        var errorCode = ErrorCode.USER_ALREADY_EXISTS;

        assertThat(errorCode.getCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(409);
        assertThat(errorCode).hasToString("USER_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Should have correct code and status for ADMIN_AUTH_REQUIRED")
    void shouldHaveCorrectCodeAndStatus_ForAdminAuthRequired() {

        var errorCode = ErrorCode.ADMIN_AUTH_REQUIRED;

        assertThat(errorCode.getCode()).isEqualTo("ADMIN_AUTH_REQUIRED");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(401);
        assertThat(errorCode).hasToString("ADMIN_AUTH_REQUIRED");
    }

    @Test
    @DisplayName("Should have correct code and status for ADMIN_AUTH_INSUFFICIENT")
    void shouldHaveCorrectCodeAndStatus_ForAdminAuthInsufficient() {

        var errorCode = ErrorCode.ADMIN_AUTH_INSUFFICIENT;

        assertThat(errorCode.getCode()).isEqualTo("ADMIN_AUTH_INSUFFICIENT");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(403);
        assertThat(errorCode).hasToString("ADMIN_AUTH_INSUFFICIENT");
    }

    @Test
    @DisplayName("Should have correct code and status for INVALID_CREDENTIALS")
    void shouldHaveCorrectCodeAndStatus_ForInvalidCredentials() {

        var errorCode = ErrorCode.INVALID_CREDENTIALS;

        assertThat(errorCode.getCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(401);
        assertThat(errorCode).hasToString("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("Should have correct code and status for USER_NOT_FOUND")
    void shouldHaveCorrectCodeAndStatus_ForUserNotFound() {

        var errorCode = ErrorCode.USER_NOT_FOUND;

        assertThat(errorCode.getCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(404);
        assertThat(errorCode).hasToString("USER_NOT_FOUND");
    }

    @Test
    @DisplayName("Should have correct code and status for INVALID_TOKEN")
    void shouldHaveCorrectCodeAndStatus_ForInvalidToken() {

        var errorCode = ErrorCode.INVALID_TOKEN;

        assertThat(errorCode.getCode()).isEqualTo("INVALID_TOKEN");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(401);
        assertThat(errorCode).hasToString("INVALID_TOKEN");
    }

    @Test
    @DisplayName("Should have correct code and status for TOKEN_EXPIRED")
    void shouldHaveCorrectCodeAndStatus_ForTokenExpired() {

        var errorCode = ErrorCode.TOKEN_EXPIRED;

        assertThat(errorCode.getCode()).isEqualTo("TOKEN_EXPIRED");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(401);
        assertThat(errorCode).hasToString("TOKEN_EXPIRED");
    }

    @Test
    @DisplayName("Should have correct code and status for VALIDATION_ERROR")
    void shouldHaveCorrectCodeAndStatus_ForValidationError() {

        var errorCode = ErrorCode.VALIDATION_ERROR;

        assertThat(errorCode.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(400);
        assertThat(errorCode).hasToString("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("Should have correct code and status for OPERATION_NOT_PERMITTED")
    void shouldHaveCorrectCodeAndStatus_ForOperationNotPermitted() {

        var errorCode = ErrorCode.OPERATION_NOT_PERMITTED;

        assertThat(errorCode.getCode()).isEqualTo("OPERATION_NOT_PERMITTED");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(403);
        assertThat(errorCode).hasToString("OPERATION_NOT_PERMITTED");
    }

    @Test
    @DisplayName("Should have correct code and status for TRANSACTION_NOT_FOUND")
    void shouldHaveCorrectCodeAndStatus_ForTransactionNotFound() {

        var errorCode = ErrorCode.TRANSACTION_NOT_FOUND;

        assertThat(errorCode.getCode()).isEqualTo("TRANSACTION_NOT_FOUND");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(404);
        assertThat(errorCode).hasToString("TRANSACTION_NOT_FOUND");
    }

    @Test
    @DisplayName("Should have correct code and status for UNAUTHORIZED_ACCESS")
    void shouldHaveCorrectCodeAndStatus_ForUnauthorizedAccess() {

        var errorCode = ErrorCode.UNAUTHORIZED_ACCESS;

        assertThat(errorCode.getCode()).isEqualTo("UNAUTHORIZED_ACCESS");
        assertThat(errorCode.getHttpStatusCode()).isEqualTo(403);
        assertThat(errorCode).hasToString("UNAUTHORIZED_ACCESS");
    }

    @Test
    @DisplayName("Should have all error codes")
    void shouldHaveAllErrorCodes() {

        var values = ErrorCode.values();

        assertThat(values)
                .hasSize(15)
                .contains(
                        ErrorCode.USER_ALREADY_EXISTS,
                        ErrorCode.ADMIN_AUTH_REQUIRED,
                        ErrorCode.ADMIN_AUTH_INSUFFICIENT,
                        ErrorCode.INVALID_CREDENTIALS,
                        ErrorCode.USER_NOT_FOUND,
                        ErrorCode.INVALID_TOKEN,
                        ErrorCode.TOKEN_EXPIRED,
                        ErrorCode.VALIDATION_ERROR,
                        ErrorCode.OPERATION_NOT_PERMITTED,
                        ErrorCode.SAVINGS_NOT_FOUND,
                        ErrorCode.INSUFFICIENT_BALANCE,
                        ErrorCode.TRANSACTION_NOT_FOUND,
                        ErrorCode.UNAUTHORIZED_ACCESS,
                        ErrorCode.TRANSFER_TRANSACTION_NOT_MODIFIABLE,
                        ErrorCode.CANNOT_DELETE_INCOME);
    }

    @Test
    @DisplayName("Should get error code by name")
    void shouldGetErrorCodeByName() {

        var errorCode = ErrorCode.valueOf("USER_NOT_FOUND");

        assertThat(errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
