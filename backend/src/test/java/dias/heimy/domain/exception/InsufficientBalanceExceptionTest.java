package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.INSUFFICIENT_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for InsufficientBalanceException")
class InsufficientBalanceExceptionTest {

    @Test
    @DisplayName("Should create exception with correct message")
    void shouldCreateException_WithCorrectMessage() {

        var exception = new InsufficientBalanceException();

        assertThat(exception.getMessage()).isEqualTo("Saldo insuficiente");
    }

    @Test
    @DisplayName("Should have correct error code")
    void shouldHaveCorrectErrorCode() {

        var exception = new InsufficientBalanceException();

        assertThat(exception.getErrorCode()).isEqualTo(INSUFFICIENT_BALANCE.getCode());
    }

    @Test
    @DisplayName("Should be instance of BusinessException")
    void shouldBeInstanceOfBusinessException() {

        var exception = new InsufficientBalanceException();

        assertThat(exception).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var exception = new InsufficientBalanceException();

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new InsufficientBalanceException();

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Should have consistent message across instances")
    void shouldHaveConsistentMessage_AcrossInstances() {

        var exception1 = new InsufficientBalanceException();
        var exception2 = new InsufficientBalanceException();

        assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
    }
}
