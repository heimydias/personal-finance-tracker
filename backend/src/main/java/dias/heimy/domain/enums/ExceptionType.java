package dias.heimy.domain.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    VALIDATION(HttpStatus.BAD_REQUEST),
    BUSINESS_RULE(HttpStatus.UNPROCESSABLE_ENTITY),
    AUTHENTICATION(HttpStatus.UNAUTHORIZED),
    AUTHORIZATION(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT);

    private final HttpStatus httpStatus;

    ExceptionType(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
