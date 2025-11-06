package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.TRANSFER_TRANSACTION_NOT_MODIFIABLE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for TransferTransactionNotModifiableException")
class TransferTransactionNotModifiableExceptionTest {

    @Test
    @DisplayName("Should create exception with correct message")
    void shouldCreateException_WithCorrectMessage() {

        var exception = new TransferTransactionNotModifiableException();

        assertThat(exception.getMessage())
                .contains("Transações do tipo TRANSFER")
                .contains("gerenciadas automaticamente")
                .contains("não podem ser modificadas ou deletadas manualmente")
                .contains("endpoints de poupança");
    }

    @Test
    @DisplayName("Should have correct error code")
    void shouldHaveCorrectErrorCode() {

        var exception = new TransferTransactionNotModifiableException();

        assertThat(exception.getErrorCode()).isEqualTo(TRANSFER_TRANSACTION_NOT_MODIFIABLE.getCode());
    }

    @Test
    @DisplayName("Should be instance of DomainException")
    void shouldBeInstanceOfDomainException() {

        var exception = new TransferTransactionNotModifiableException();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var exception = new TransferTransactionNotModifiableException();

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new TransferTransactionNotModifiableException();

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Should have consistent message across instances")
    void shouldHaveConsistentMessage_AcrossInstances() {

        var exception1 = new TransferTransactionNotModifiableException();
        var exception2 = new TransferTransactionNotModifiableException();

        assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
    }
}
