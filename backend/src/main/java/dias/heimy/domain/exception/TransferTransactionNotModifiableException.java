package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.TRANSFER_TRANSACTION_NOT_MODIFIABLE;

import java.io.Serial;

public class TransferTransactionNotModifiableException extends BusinessException {

    private static final String MESSAGE =
            "Transações do tipo TRANSFER são gerenciadas automaticamente pelo sistema e não podem ser modificadas ou deletadas manualmente. Use os endpoints de poupança para gerenciar suas economias.";

    @Serial
    private static final long serialVersionUID = -3847392847392847392L;

    public TransferTransactionNotModifiableException() {
        super(TRANSFER_TRANSACTION_NOT_MODIFIABLE, MESSAGE);
    }
}
