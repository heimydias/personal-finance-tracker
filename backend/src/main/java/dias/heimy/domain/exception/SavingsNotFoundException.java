package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.SAVINGS_NOT_FOUND;

import java.io.Serial;
import java.util.UUID;

public class SavingsNotFoundException extends DomainException {

    private static final String MESSAGE_TEMPLATE = "Poupança não encontrada: %s";

    @Serial
    private static final long serialVersionUID = -4709037063324458276L;

    public SavingsNotFoundException(UUID savingsId) {
        super(SAVINGS_NOT_FOUND, String.format(MESSAGE_TEMPLATE, savingsId));
    }
}
