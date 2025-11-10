package dias.heimy.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Tests for UserNotFoundException")
class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with email")
    void shouldCreateException_WithEmail() {
        var email = "test@example.com";

        var exception = new UserNotFoundException(email);

        assertThat(exception.getMessage()).isEqualTo("Usuário não encontrado: " + email);
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should have correct error properties")
    void shouldHaveCorrectErrorProperties() {
        var exception = new UserNotFoundException("Test message");

        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getErrorCode()).isNotNull();
        assertThat(exception.getHttpStatusCode()).isEqualTo(404);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
