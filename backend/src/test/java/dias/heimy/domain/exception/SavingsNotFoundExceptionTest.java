package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.SAVINGS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for SavingsNotFoundException")
class SavingsNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with savings ID in message")
    void shouldCreateException_WithSavingsIdInMessage() {

        var savingsId = UUID.randomUUID();
        var exception = new SavingsNotFoundException(savingsId);

        assertThat(exception.getMessage()).contains("Poupança não encontrada").contains(savingsId.toString());
    }

    @Test
    @DisplayName("Should have correct error code")
    void shouldHaveCorrectErrorCode() {

        var savingsId = UUID.randomUUID();
        var exception = new SavingsNotFoundException(savingsId);

        assertThat(exception.getErrorCode()).isEqualTo(SAVINGS_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("Should be instance of DomainException")
    void shouldBeInstanceOfDomainException() {

        var savingsId = UUID.randomUUID();
        var exception = new SavingsNotFoundException(savingsId);

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var savingsId = UUID.randomUUID();
        var exception = new SavingsNotFoundException(savingsId);

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var savingsId = UUID.randomUUID();
        var exception = new SavingsNotFoundException(savingsId);

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Should create different messages for different savings IDs")
    void shouldCreateDifferentMessages_ForDifferentSavingsIds() {

        var savingsId1 = UUID.randomUUID();
        var savingsId2 = UUID.randomUUID();

        var exception1 = new SavingsNotFoundException(savingsId1);
        var exception2 = new SavingsNotFoundException(savingsId2);

        assertThat(exception1.getMessage()).isNotEqualTo(exception2.getMessage());
        assertThat(exception1.getMessage()).contains(savingsId1.toString());
        assertThat(exception2.getMessage()).contains(savingsId2.toString());
    }
}
