package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.INVALID_TOKEN;

import java.io.Serial;

public class InvalidTokenException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472967899243578623L;

    public InvalidTokenException(String message) {
        super(INVALID_TOKEN, message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(INVALID_TOKEN, message, cause);
    }
}
