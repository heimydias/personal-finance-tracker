package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.CANNOT_DELETE_INCOME;

import java.io.Serial;

public class CannotDeleteIncomeException extends BusinessException {

    private static final String MESSAGE = "Não é possível deletar esta receita pois resultaria em saldo negativo. "
            + "Para deletar esta receita, primeiro você precisa remover despesas ou resgatar poupanças que dependem dela.";

    @Serial
    private static final long serialVersionUID = -2847392847392847392L;

    public CannotDeleteIncomeException() {
        super(CANNOT_DELETE_INCOME, MESSAGE);
    }
}
