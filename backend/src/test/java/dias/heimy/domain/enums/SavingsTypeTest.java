package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for SavingsType")
class SavingsTypeTest {

    @Test
    @DisplayName("Should have all expected values")
    void shouldHaveAllExpectedValues() {

        var values = SavingsType.values();

        assertThat(values)
                .hasSize(8)
                .contains(
                        SavingsType.EMERGENCY_FUND,
                        SavingsType.RETIREMENT,
                        SavingsType.TRAVEL,
                        SavingsType.INVESTMENT,
                        SavingsType.EDUCATION,
                        SavingsType.HOME,
                        SavingsType.VEHICLE,
                        SavingsType.OTHER);
    }

    @Test
    @DisplayName("Should get EMERGENCY_FUND by name")
    void shouldGetEmergencyFundByName() {

        var savingsType = SavingsType.valueOf("EMERGENCY_FUND");

        assertThat(savingsType).isEqualTo(SavingsType.EMERGENCY_FUND);
    }

    @Test
    @DisplayName("Should have correct description for EMERGENCY_FUND")
    void shouldHaveCorrectDescription_ForEmergencyFund() {

        var savingsType = SavingsType.EMERGENCY_FUND;

        assertThat(savingsType.getDescription()).isEqualTo("Reserva de Emergência");
    }

    @Test
    @DisplayName("Should have correct description for RETIREMENT")
    void shouldHaveCorrectDescription_ForRetirement() {

        var savingsType = SavingsType.RETIREMENT;

        assertThat(savingsType.getDescription()).isEqualTo("Aposentadoria");
    }

    @Test
    @DisplayName("Should have correct description for TRAVEL")
    void shouldHaveCorrectDescription_ForTravel() {

        var savingsType = SavingsType.TRAVEL;

        assertThat(savingsType.getDescription()).isEqualTo("Viagem");
    }

    @Test
    @DisplayName("Should have correct description for INVESTMENT")
    void shouldHaveCorrectDescription_ForInvestment() {

        var savingsType = SavingsType.INVESTMENT;

        assertThat(savingsType.getDescription()).isEqualTo("Investimento");
    }

    @Test
    @DisplayName("Should have correct description for EDUCATION")
    void shouldHaveCorrectDescription_ForEducation() {

        var savingsType = SavingsType.EDUCATION;

        assertThat(savingsType.getDescription()).isEqualTo("Educação");
    }

    @Test
    @DisplayName("Should have correct description for HOME")
    void shouldHaveCorrectDescription_ForHome() {

        var savingsType = SavingsType.HOME;

        assertThat(savingsType.getDescription()).isEqualTo("Casa/Imóvel");
    }

    @Test
    @DisplayName("Should have correct description for VEHICLE")
    void shouldHaveCorrectDescription_ForVehicle() {

        var savingsType = SavingsType.VEHICLE;

        assertThat(savingsType.getDescription()).isEqualTo("Veículo");
    }

    @Test
    @DisplayName("Should have correct description for OTHER")
    void shouldHaveCorrectDescription_ForOther() {

        var savingsType = SavingsType.OTHER;

        assertThat(savingsType.getDescription()).isEqualTo("Outros");
    }

    @Test
    @DisplayName("Should verify enum constants are not null")
    void shouldVerifyEnumConstantsAreNotNull() {

        assertThat(SavingsType.EMERGENCY_FUND).isNotNull();
        assertThat(SavingsType.RETIREMENT).isNotNull();
        assertThat(SavingsType.TRAVEL).isNotNull();
        assertThat(SavingsType.INVESTMENT).isNotNull();
        assertThat(SavingsType.EDUCATION).isNotNull();
        assertThat(SavingsType.HOME).isNotNull();
        assertThat(SavingsType.VEHICLE).isNotNull();
        assertThat(SavingsType.OTHER).isNotNull();
    }

    @Test
    @DisplayName("Should verify all enum constants are unique")
    void shouldVerifyAllEnumConstantsAreUnique() {

        assertThat(SavingsType.EMERGENCY_FUND).isNotEqualTo(SavingsType.RETIREMENT);
        assertThat(SavingsType.TRAVEL).isNotEqualTo(SavingsType.INVESTMENT);
        assertThat(SavingsType.EDUCATION).isNotEqualTo(SavingsType.HOME);
        assertThat(SavingsType.VEHICLE).isNotEqualTo(SavingsType.OTHER);
    }
}
