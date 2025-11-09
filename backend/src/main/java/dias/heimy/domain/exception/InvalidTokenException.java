package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ErrorCode;
import java.io.Serial;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472967899243578623L;

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_TOKEN;

    @Override
    public String getErrorCode() {
        return ERROR_CODE.getCode();
    }

    @Override
    public int getHttpStatusCode() {
        return ERROR_CODE.getHttpStatusCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return ERROR_CODE.getHttpStatus();
    }
}
