package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ExceptionType;
import java.io.Serial;

public abstract class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6098402741363463495L;

    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getErrorCode();

    public int getHttpStatusCode() {
        return 400;
    }

    public abstract ExceptionType getType();
}
