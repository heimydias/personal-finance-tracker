package dias.heimy.domain.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;

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
        return HttpStatus.BAD_REQUEST.value();
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(getHttpStatusCode());
    }
}
