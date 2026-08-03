package com.songisuk.portfolio.authbridge.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorWriter errors;

    public ApiAccessDeniedHandler(SecurityErrorWriter errors) {
        this.errors = errors;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        boolean csrfFailure = accessDeniedException instanceof MissingCsrfTokenException
                || accessDeniedException instanceof InvalidCsrfTokenException;
        if (csrfFailure) {
            errors.write(response, HttpStatus.FORBIDDEN.value(), "INVALID_CSRF",
                    "A valid CSRF token is required");
            return;
        }
        errors.write(response, HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED",
                "The authenticated user does not have the required role");
    }
}
