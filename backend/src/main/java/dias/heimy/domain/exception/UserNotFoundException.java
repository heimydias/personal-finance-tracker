package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.USER_NOT_FOUND;

import java.io.Serial;

public class UserNotFoundException extends BusinessException {

    private static final String MESSAGE_TEMPLATE = "Usuário não encontrado: %s";

    @Serial
    private static final long serialVersionUID = -5141929566138546373L;

    public UserNotFoundException(String email) {
        super(USER_NOT_FOUND, String.format(MESSAGE_TEMPLATE, email));
    }
}
