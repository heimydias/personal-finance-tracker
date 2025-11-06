package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for TransferType")
class TransferTypeTest {

    @Test
    @DisplayName("Should have DEPOSIT and WITHDRAWAL values")
    void shouldHaveDepositAndWithdrawalValues() {

        var values = TransferType.values();

        assertThat(values).hasSize(2).contains(TransferType.DEPOSIT, TransferType.WITHDRAWAL);
    }

    @Test
    @DisplayName("Should get DEPOSIT by name")
    void shouldGetDepositByName() {

        var transferType = TransferType.valueOf("DEPOSIT");

        assertThat(transferType).isEqualTo(TransferType.DEPOSIT);
    }

    @Test
    @DisplayName("Should get WITHDRAWAL by name")
    void shouldGetWithdrawalByName() {

        var transferType = TransferType.valueOf("WITHDRAWAL");

        assertThat(transferType).isEqualTo(TransferType.WITHDRAWAL);
    }

    @Test
    @DisplayName("Should have correct description for DEPOSIT")
    void shouldHaveCorrectDescription_ForDeposit() {

        var transferType = TransferType.DEPOSIT;

        assertThat(transferType.getDescription()).isEqualTo("Depósito em Poupança");
    }

    @Test
    @DisplayName("Should have correct description for WITHDRAWAL")
    void shouldHaveCorrectDescription_ForWithdrawal() {

        var transferType = TransferType.WITHDRAWAL;

        assertThat(transferType.getDescription()).isEqualTo("Resgate de Poupança");
    }

    @Test
    @DisplayName("Should verify enum constants are not null")
    void shouldVerifyEnumConstantsAreNotNull() {

        assertThat(TransferType.DEPOSIT).isNotNull();
        assertThat(TransferType.WITHDRAWAL).isNotNull();
    }

    @Test
    @DisplayName("Should verify enum constants are unique")
    void shouldVerifyEnumConstantsAreUnique() {

        assertThat(TransferType.DEPOSIT).isNotEqualTo(TransferType.WITHDRAWAL);
    }
}
