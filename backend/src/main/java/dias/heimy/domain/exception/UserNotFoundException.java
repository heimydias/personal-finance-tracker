package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ErrorCode;
import dias.heimy.domain.enums.ExceptionType;
import java.io.Serial;

public class UserNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -8472956724834578234L;

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_NOT_FOUND;

    @Override
    public String getErrorCode() {
        return ERROR_CODE.getCode();
    }

    @Override
    public int getHttpStatusCode() {
        return ERROR_CODE.getHttpStatusCode();
    }

    @Override
    public ExceptionType getType() {
        return ExceptionType.NOT_FOUND;
    }
}
