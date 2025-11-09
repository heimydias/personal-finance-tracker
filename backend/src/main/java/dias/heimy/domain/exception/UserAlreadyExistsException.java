package dias.heimy.domain.exception;

import dias.heimy.domain.enums.ErrorCode;
import java.io.Serial;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BusinessException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_ALREADY_EXISTS;

    @Serial
    private static final long serialVersionUID = -4227606701914326086L;

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }

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
