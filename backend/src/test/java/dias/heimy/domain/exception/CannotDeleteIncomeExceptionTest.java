package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.CANNOT_DELETE_INCOME;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for CannotDeleteIncomeException")
class CannotDeleteIncomeExceptionTest {

    @Test
    @DisplayName("Should create exception with correct message")
    void shouldCreateException_WithCorrectMessage() {

        var exception = new CannotDeleteIncomeException();

        assertThat(exception.getMessage())
                .contains("Não é possível deletar esta receita")
                .contains("saldo negativo")
                .contains("remover despesas ou resgatar poupanças");
    }

    @Test
    @DisplayName("Should have correct error code")
    void shouldHaveCorrectErrorCode() {

        var exception = new CannotDeleteIncomeException();

        assertThat(exception.getErrorCode()).isEqualTo(CANNOT_DELETE_INCOME.getCode());
    }

    @Test
    @DisplayName("Should be instance of DomainException")
    void shouldBeInstanceOfDomainException() {

        var exception = new CannotDeleteIncomeException();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeInstanceOfRuntimeException() {

        var exception = new CannotDeleteIncomeException();

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should have serial version UID")
    void shouldHaveSerialVersionUID() {

        var exception = new CannotDeleteIncomeException();

        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Should have consistent message across instances")
    void shouldHaveConsistentMessage_AcrossInstances() {

        var exception1 = new CannotDeleteIncomeException();
        var exception2 = new CannotDeleteIncomeException();

        assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
    }
}
