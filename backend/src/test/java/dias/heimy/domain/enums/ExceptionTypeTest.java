package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Tests for ExceptionType")
class ExceptionTypeTest {

    @Test
    @DisplayName("Should have all expected values")
    void shouldHaveAllExpectedValues() {

        var values = ExceptionType.values();

        assertThat(values)
                .hasSize(6)
                .contains(
                        ExceptionType.VALIDATION,
                        ExceptionType.BUSINESS_RULE,
                        ExceptionType.AUTHENTICATION,
                        ExceptionType.AUTHORIZATION,
                        ExceptionType.NOT_FOUND,
                        ExceptionType.CONFLICT);
    }

    @Test
    @DisplayName("Should get VALIDATION by name")
    void shouldGetValidationByName() {

        var exceptionType = ExceptionType.valueOf("VALIDATION");

        assertThat(exceptionType).isEqualTo(ExceptionType.VALIDATION);
    }

    @Test
    @DisplayName("Should have correct HTTP status for VALIDATION")
    void shouldHaveCorrectHttpStatus_ForValidation() {

        var exceptionType = ExceptionType.VALIDATION;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should have correct HTTP status for BUSINESS_RULE")
    void shouldHaveCorrectHttpStatus_ForBusinessRule() {

        var exceptionType = ExceptionType.BUSINESS_RULE;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(422);
    }

    @Test
    @DisplayName("Should have correct HTTP status for AUTHENTICATION")
    void shouldHaveCorrectHttpStatus_ForAuthentication() {

        var exceptionType = ExceptionType.AUTHENTICATION;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should have correct HTTP status for AUTHORIZATION")
    void shouldHaveCorrectHttpStatus_ForAuthorization() {

        var exceptionType = ExceptionType.AUTHORIZATION;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should have correct HTTP status for NOT_FOUND")
    void shouldHaveCorrectHttpStatus_ForNotFound() {

        var exceptionType = ExceptionType.NOT_FOUND;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should have correct HTTP status for CONFLICT")
    void shouldHaveCorrectHttpStatus_ForConflict() {

        var exceptionType = ExceptionType.CONFLICT;

        assertThat(exceptionType.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exceptionType.getHttpStatusCode()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should verify enum constants are not null")
    void shouldVerifyEnumConstantsAreNotNull() {

        assertThat(ExceptionType.VALIDATION).isNotNull();
        assertThat(ExceptionType.BUSINESS_RULE).isNotNull();
        assertThat(ExceptionType.AUTHENTICATION).isNotNull();
        assertThat(ExceptionType.AUTHORIZATION).isNotNull();
        assertThat(ExceptionType.NOT_FOUND).isNotNull();
        assertThat(ExceptionType.CONFLICT).isNotNull();
    }

    @Test
    @DisplayName("Should verify all enum constants are unique")
    void shouldVerifyAllEnumConstantsAreUnique() {

        assertThat(ExceptionType.VALIDATION).isNotEqualTo(ExceptionType.BUSINESS_RULE);
        assertThat(ExceptionType.AUTHENTICATION).isNotEqualTo(ExceptionType.AUTHORIZATION);
        assertThat(ExceptionType.NOT_FOUND).isNotEqualTo(ExceptionType.CONFLICT);
    }
}
