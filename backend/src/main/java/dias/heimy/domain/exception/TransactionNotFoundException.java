package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.TRANSACTION_NOT_FOUND;

import java.io.Serial;
import java.util.UUID;

public class TransactionNotFoundException extends BusinessException {

    private static final String MESSAGE_TEMPLATE = "Transação não encontrada: %s";

    @Serial
    private static final long serialVersionUID = -5128018954992406688L;

    public TransactionNotFoundException(UUID transactionId) {
        super(TRANSACTION_NOT_FOUND, String.format(MESSAGE_TEMPLATE, transactionId));
    }
}
