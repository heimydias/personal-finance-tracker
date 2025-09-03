package dias.heimy.domain.exception;

import java.io.Serial;

public class UserAlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    @Serial
    private static final long serialVersionUID = -4227606701914326086L;

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public int getHttpStatusCode() {
        return 409;
    }
}
