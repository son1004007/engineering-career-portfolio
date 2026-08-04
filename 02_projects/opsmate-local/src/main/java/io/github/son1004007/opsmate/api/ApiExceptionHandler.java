package io.github.son1004007.opsmate.api;

import java.net.URI;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OpsMateException.class)
    ProblemDetail handleOpsMate(OpsMateException exception) {
        HttpStatus status = statusFor(exception.getCode());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problem.setTitle(exception.getCode().name());
        problem.setType(URI.create("urn:opsmate:error:" + exception.getCode().name().toLowerCase()));
        problem.setProperty("code", exception.getCode().name());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException exception) {
        return simple(HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED_ACTION, "The authenticated role cannot perform this action");
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class
    })
    ProblemDetail handleBadRequest(Exception exception) {
        return simple(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Request validation failed");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleConstraint(DataIntegrityViolationException exception) {
        return simple(HttpStatus.CONFLICT, ErrorCode.WRITE_CONFLICT, "A database constraint rejected the write");
    }

    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    ProblemDetail handleOptimisticConflict(Exception exception) {
        return simple(HttpStatus.CONFLICT, ErrorCode.WRITE_CONFLICT, "The resource changed during this write");
    }

    private ProblemDetail simple(HttpStatus status, ErrorCode code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(code.name());
        problem.setProperty("code", code.name());
        return problem;
    }

    private HttpStatus statusFor(ErrorCode code) {
        return switch (code) {
            case UNAUTHORIZED_ACTION -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_STATE, IDEMPOTENCY_CONFLICT, DUPLICATE_ORDER, WRITE_CONFLICT -> HttpStatus.CONFLICT;
            case MODEL_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case INVALID_MODEL_OUTPUT -> HttpStatus.BAD_GATEWAY;
            case POLICY_NOT_FOUND, VALIDATION_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY;
            case ORDER_FINALIZATION_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
            case SESSION_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case DEMO_CLOSED, DEMO_CAPACITY_REACHED -> HttpStatus.SERVICE_UNAVAILABLE;
            case RATE_LIMITED, MODEL_BUSY -> HttpStatus.TOO_MANY_REQUESTS;
        };
    }
}
