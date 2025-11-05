package dias.heimy.domain.enums;

public enum SavingsType {
    EMERGENCY_FUND("Reserva de Emergência"),
    RETIREMENT("Aposentadoria"),
    TRAVEL("Viagem"),
    INVESTMENT("Investimento"),
    EDUCATION("Educação"),
    HOME("Casa/Imóvel"),
    VEHICLE("Veículo"),
    OTHER("Outros");

    private final String description;

    SavingsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
