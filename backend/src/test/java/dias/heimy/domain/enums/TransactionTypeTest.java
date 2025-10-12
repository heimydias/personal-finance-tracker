package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for TransactionType")
class TransactionTypeTest {

    @Test
    @DisplayName("Should have INCOME and EXPENSE values")
    void shouldHaveIncomeAndExpenseValues() {

        var values = TransactionType.values();

        assertThat(values).hasSize(2).contains(TransactionType.INCOME, TransactionType.EXPENSE);
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
    @DisplayName("Should verify enum constants")
    void shouldVerifyEnumConstants() {

        assertThat(TransactionType.INCOME).isNotNull();
        assertThat(TransactionType.EXPENSE).isNotNull();
        assertThat(TransactionType.INCOME).isNotEqualTo(TransactionType.EXPENSE);
    }
}
