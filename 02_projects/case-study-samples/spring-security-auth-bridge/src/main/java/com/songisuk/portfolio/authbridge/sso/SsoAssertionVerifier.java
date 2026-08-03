package com.songisuk.portfolio.authbridge.sso;

public interface SsoAssertionVerifier {

    String verify(SsoLoginRequest assertion);
}
