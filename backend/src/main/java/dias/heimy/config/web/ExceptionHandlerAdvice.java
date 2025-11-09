package dias.heimy.config.web;

import static dias.heimy.constants.ExceptionHandlerAdviceConstants.ERROR_CODE_PROPERTY;
import static dias.heimy.constants.ExceptionHandlerAdviceConstants.TIMESTAMP_PROPERTY;
import static java.lang.String.format;
import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;
import static org.springframework.http.ResponseEntity.status;

import dias.heimy.domain.enums.ErrorCode;
import dias.heimy.domain.exception.BusinessException;
import dias.heimy.domain.exception.DomainException;
import dias.heimy.domain.exception.InvalidTokenException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException exception) {
        HttpStatus status = exception.getHttpStatus();
        ProblemDetail problemDetail = forStatusAndDetail(status, exception.getMessage());
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, exception.getErrorCode());
        problemDetail.setProperty("httpStatus", status.name());

        if ((status == UNAUTHORIZED || status == CONFLICT) && exception instanceof DomainException domainEx) {
            problemDetail.setProperty("errorCode", domainEx.getErrorCode());
        }

        return status(status).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();

        List<String> objectErrors = exception.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        String mainMessage;
        if (!objectErrors.isEmpty()) {
            mainMessage = objectErrors.getFirst();
        } else if (!fieldErrors.isEmpty()) {
            mainMessage = "Validation failed for fields";
        } else {
            mainMessage = "Validation failed for argument";
        }

        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, mainMessage);
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("violations", objectErrors);
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.VALIDATION_ERROR.getCode());

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, "Malformed JSON request");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty("detail", exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    ProblemDetail handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException exception) {
        return exceptionToProblemDetailForStatusAndDetail(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleHttpClientErrorExceptionNotFound(ResourceAccessException exception) {
        return exceptionToProblemDetailForStatusAndDetail(SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return exceptionToProblemDetailForStatusAndDetail(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> violations = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        String mainMessage = violations.isEmpty() ? "Validation failed" : violations.getFirst();

        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, mainMessage);
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty("violations", violations);
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.VALIDATION_ERROR.getCode());

        return status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException exception) {
        ProblemDetail problemDetail =
                exceptionToProblemDetailForStatusAndDetail(exception.getStatusCode(), exception.getReason());
        return status(exception.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception) {
        return exceptionToProblemDetailForStatusAndDetail(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        ProblemDetail problemDetail =
                exceptionToProblemDetailForStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.INVALID_CREDENTIALS.getCode());
        return status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException exception) {
        ProblemDetail problemDetail =
                exceptionToProblemDetailForStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.INVALID_CREDENTIALS.getCode());
        return status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidTokenException(InvalidTokenException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getHttpStatusCode());
        ProblemDetail problemDetail = forStatusAndDetail(status, exception.getMessage());
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, exception.getErrorCode());
        return status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        return exceptionToProblemDetailForStatusAndDetail(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ProblemDetail exceptionToProblemDetailForStatusAndDetail(HttpStatusCode status, String detail) {
        ProblemDetail problemDetail = forStatusAndDetail(status, detail);
        problemDetail.setProperty(TIMESTAMP_PROPERTY, now());
        return problemDetail;
    }
}
