package com.songisuk.portfolio.rules.error;

public class MissingIdentityException extends RuntimeException {
    public MissingIdentityException() {
        super("authenticated subject is missing");
    }
}
