package dias.heimy.domain.exception;

import static dias.heimy.domain.enums.ErrorCode.UNAUTHORIZED_ACCESS;

import java.io.Serial;

public class UnauthorizedAccessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -6616861306517991240L;

    public UnauthorizedAccessException(String resourceType) {
        super(UNAUTHORIZED_ACCESS, String.format("Você não tem permissão para acessar %s", resourceType));
    }
}
