package dias.heimy.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", 409),
    ADMIN_AUTH_REQUIRED("ADMIN_AUTH_REQUIRED", 401),
    ADMIN_AUTH_INSUFFICIENT("ADMIN_AUTH_INSUFFICIENT", 403),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", 401),
    USER_NOT_FOUND("USER_NOT_FOUND", 404),
    INVALID_TOKEN("INVALID_TOKEN", 401),
    TOKEN_EXPIRED("TOKEN_EXPIRED", 401),
    VALIDATION_ERROR("VALIDATION_ERROR", 400),
    OPERATION_NOT_PERMITTED("OPERATION_NOT_PERMITTED", 403),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND", 404),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", 403);

    private final String code;
    private final int httpStatusCode;

    ErrorCode(String code, int httpStatusCode) {
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        return code;
    }
}
