package com.songisuk.portfolio.authbridge.sso;

import java.time.Duration;
import java.time.Instant;

public interface ReplayNonceStore {

    boolean markIfUnused(String nonce, Instant now, Duration retention);
}
