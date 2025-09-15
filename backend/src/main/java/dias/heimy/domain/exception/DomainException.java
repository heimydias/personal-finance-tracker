package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ErrorCode;
import dias.heimy.domain.enums.ExceptionType;
import java.io.Serial;
import lombok.Getter;

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
    public ExceptionType getType() {
        return switch (errorCode) {
            case USER_ALREADY_EXISTS, USER_NOT_FOUND -> ExceptionType.CONFLICT;
            case ADMIN_AUTH_REQUIRED, ADMIN_AUTH_INSUFFICIENT, INVALID_CREDENTIALS, INVALID_TOKEN, TOKEN_EXPIRED ->
                ExceptionType.AUTHENTICATION;
            case VALIDATION_ERROR -> ExceptionType.VALIDATION;
        };
    }
}
