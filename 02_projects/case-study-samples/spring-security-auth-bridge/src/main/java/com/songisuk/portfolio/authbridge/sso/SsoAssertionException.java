package com.songisuk.portfolio.authbridge.sso;

import org.springframework.security.core.AuthenticationException;

public class SsoAssertionException extends AuthenticationException {

    public SsoAssertionException(String message) {
        super(message);
    }
}
