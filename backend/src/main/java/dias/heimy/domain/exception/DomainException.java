package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ErrorCode;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472967899243578627L;

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode.getCode();
    }

    @Override
    public int getHttpStatusCode() {
        return errorCode.getHttpStatusCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
