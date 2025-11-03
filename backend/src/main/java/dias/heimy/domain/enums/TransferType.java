package dias.heimy.domain.enums;

import lombok.Getter;

/**
 * Enum que representa o tipo de transferência para transações do tipo TRANSFER.
 * Usado para distinguir entre depósitos em poupança e resgates de poupança.
 */
@Getter
public enum TransferType {
    DEPOSIT("Depósito em Poupança"),
    WITHDRAWAL("Resgate de Poupança");

    private final String description;

    TransferType(String description) {
        this.description = description;
    }
}
