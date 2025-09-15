package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ExceptionType.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for UserNotFoundException")
class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with email")
    void shouldCreateException_WithEmail() {
        var email = "test@example.com";

        var exception = new UserNotFoundException(email);

        assertThat(exception.getMessage()).isEqualTo("User not found with email: " + email);
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception.getType()).isEqualTo(NOT_FOUND);
    }

    @Test
    @DisplayName("Should have correct error properties")
    void shouldHaveCorrectErrorProperties() {
        var exception = new UserNotFoundException("Test message");

        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getErrorCode()).isNotNull();
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception.getType()).isEqualTo(NOT_FOUND);
    }
}
