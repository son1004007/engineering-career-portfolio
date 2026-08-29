package com.songisuk.portfolio.rules.web;

import com.songisuk.portfolio.rules.error.MissingIdentityException;
import com.songisuk.portfolio.rules.error.SnapshotNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MissingIdentityException.class)
    public ProblemDetail missingIdentity(MissingIdentityException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setTitle("Authenticated subject required");
        detail.setDetail(exception.getMessage());
        return detail;
    }

    @ExceptionHandler(SnapshotNotFoundException.class)
    public ProblemDetail snapshotMissing(SnapshotNotFoundException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Snapshot unavailable");
        detail.setDetail(exception.getMessage());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail invalidSnapshot(IllegalArgumentException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Invalid snapshot request");
        detail.setDetail(exception.getMessage());
        return detail;
    }
}
