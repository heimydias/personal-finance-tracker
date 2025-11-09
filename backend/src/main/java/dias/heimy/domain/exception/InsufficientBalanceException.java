package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.INSUFFICIENT_BALANCE;

import java.io.Serial;

public class InsufficientBalanceException extends BusinessException {

    private static final String MESSAGE = "Saldo insuficiente";

    @Serial
    private static final long serialVersionUID = -6073540538612008268L;

    public InsufficientBalanceException() {
        super(INSUFFICIENT_BALANCE, MESSAGE);
    }
}
