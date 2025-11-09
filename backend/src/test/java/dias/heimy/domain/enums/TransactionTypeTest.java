package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for TransactionType")
class TransactionTypeTest {

    @Test
    @DisplayName("Should have INCOME, EXPENSE and TRANSFER values")
    void shouldHaveIncomeAndExpenseValues() {

        var values = TransactionType.values();

        assertThat(values)
                .hasSize(3)
                .contains(TransactionType.INCOME, TransactionType.EXPENSE, TransactionType.TRANSFER);
    }

    @Test
    @DisplayName("Should get INCOME by name")
    void shouldGetIncomeByName() {

        var transactionType = TransactionType.valueOf("INCOME");

        assertThat(transactionType).isEqualTo(TransactionType.INCOME);
    }

    @Test
    @DisplayName("Should get EXPENSE by name")
    void shouldGetExpenseByName() {

        var transactionType = TransactionType.valueOf("EXPENSE");

        assertThat(transactionType).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    @DisplayName("Should get TRANSFER by name")
    void shouldGetTransferByName() {

        var transactionType = TransactionType.valueOf("TRANSFER");

        assertThat(transactionType).isEqualTo(TransactionType.TRANSFER);
    }

    @Test
    @DisplayName("Should verify enum constants")
    void shouldVerifyEnumConstants() {

        assertThat(TransactionType.values()).doesNotContainNull().doesNotHaveDuplicates();
    }
}
