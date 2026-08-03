package com.songisuk.portfolio.authbridge.web;

import com.songisuk.portfolio.authbridge.sso.SsoAdapterUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(SsoAdapterUnavailableException.class)
    ResponseEntity<ApiError> ssoUnavailable(SsoAdapterUnavailableException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "SSO_ADAPTER_UNAVAILABLE",
                "SSO authentication is not safely configured");
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> authenticationFailed(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
                "Credentials or SSO assertion could not be verified");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiError> invalidRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request body is invalid");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(code, message, clock.instant()));
    }
}
