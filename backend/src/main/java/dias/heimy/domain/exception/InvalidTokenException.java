package dias.heimy.domain.exception;

import java.io.Serial;

public class InvalidTokenException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472967899243578623L;

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_TOKEN";
    }

    @Override
    public int getHttpStatusCode() {
        return 401;
    }
}
