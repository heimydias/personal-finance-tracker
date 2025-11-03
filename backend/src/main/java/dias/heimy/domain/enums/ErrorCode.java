package dias.heimy.domain.enums;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", CONFLICT),
    ADMIN_AUTH_REQUIRED("ADMIN_AUTH_REQUIRED", UNAUTHORIZED),
    ADMIN_AUTH_INSUFFICIENT("ADMIN_AUTH_INSUFFICIENT", FORBIDDEN),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", UNAUTHORIZED),
    USER_NOT_FOUND("USER_NOT_FOUND", NOT_FOUND),
    INVALID_TOKEN("INVALID_TOKEN", UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", UNAUTHORIZED),
    VALIDATION_ERROR("VALIDATION_ERROR", BAD_REQUEST),
    OPERATION_NOT_PERMITTED("OPERATION_NOT_PERMITTED", FORBIDDEN),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND", NOT_FOUND),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", FORBIDDEN),
    SAVINGS_NOT_FOUND("SAVINGS_NOT_FOUND", NOT_FOUND),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", BAD_REQUEST),
    TRANSFER_TRANSACTION_NOT_MODIFIABLE("TRANSFER_TRANSACTION_NOT_MODIFIABLE", FORBIDDEN),
    CANNOT_DELETE_INCOME("CANNOT_DELETE_INCOME", BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatus.value();
    }

    @Override
    public String toString() {
        return code;
    }
}
