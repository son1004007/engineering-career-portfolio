package com.songisuk.portfolio.authbridge.config;

import com.songisuk.portfolio.authbridge.sso.SsoAdapterUnavailableException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ConfigurationProperties("app.sso")
public class SsoProperties {

    private String sharedSecret;
    private String expectedIssuer;
    private String expectedAudience;
    private String activeKeyId;
    private Duration maxAge = Duration.ofMinutes(2);
    private Duration maxFutureSkew = Duration.ofSeconds(10);

    public byte[] requiredSecretBytes() {
        if (sharedSecret == null || sharedSecret.isBlank()) {
            throw new SsoAdapterUnavailableException("SSO verifier is not configured");
        }

        byte[] bytes = sharedSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new SsoAdapterUnavailableException("SSO verifier configuration is too weak");
        }
        return bytes;
    }

    public Duration maxAge() {
        return requirePositive(maxAge, "app.sso.max-age");
    }

    public String expectedIssuer() {
        return requireIdentifier(expectedIssuer, "app.sso.expected-issuer", 256);
    }

    public String expectedAudience() {
        return requireIdentifier(expectedAudience, "app.sso.expected-audience", 256);
    }

    public String activeKeyId() {
        return requireIdentifier(activeKeyId, "app.sso.active-key-id", 64);
    }

    public Duration maxFutureSkew() {
        if (maxFutureSkew == null || maxFutureSkew.isNegative()) {
            throw new IllegalStateException("app.sso.max-future-skew must not be negative");
        }
        return maxFutureSkew;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public void setExpectedIssuer(String expectedIssuer) {
        this.expectedIssuer = expectedIssuer;
    }

    public void setExpectedAudience(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    public void setActiveKeyId(String activeKeyId) {
        this.activeKeyId = activeKeyId;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }

    public void setMaxFutureSkew(Duration maxFutureSkew) {
        this.maxFutureSkew = maxFutureSkew;
    }

    private static Duration requirePositive(Duration duration, String property) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new IllegalStateException(property + " must be positive");
        }
        return duration;
    }

    private static String requireIdentifier(String value, String property, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            throw new SsoAdapterUnavailableException(property + " is not safely configured");
        }
        return value;
    }
}
