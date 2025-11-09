package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.USER_ALREADY_EXISTS;

import java.io.Serial;

public class UserAlreadyExistsException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -4227606701914326086L;

    public UserAlreadyExistsException(String email) {
        super(USER_ALREADY_EXISTS, "User already exists with email: " + email);
    }
}
