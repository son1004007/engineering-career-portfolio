package com.songisuk.portfolio.authbridge.sso;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryReplayNonceStore implements ReplayNonceStore {

    private final ConcurrentHashMap<String, Instant> usedUntil = new ConcurrentHashMap<>();

    @Override
    public boolean markIfUnused(String nonce, Instant now, Duration retention) {
        usedUntil.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
        return usedUntil.putIfAbsent(nonce, now.plus(retention)) == null;
    }
}
