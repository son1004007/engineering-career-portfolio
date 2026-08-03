package com.songisuk.portfolio.authbridge.sso;

import com.songisuk.portfolio.authbridge.config.SsoProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;

@Service
public class HmacSsoAssertionVerifier implements SsoAssertionVerifier {

    private static final Pattern ISSUER_OR_AUDIENCE = Pattern.compile("[A-Za-z0-9._:/-]{1,256}");
    private static final Pattern KEY_ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");
    private static final Pattern SUBJECT = Pattern.compile("[A-Za-z0-9._:@-]{1,128}");
    private static final Pattern NONCE = Pattern.compile("[A-Za-z0-9_-]{16,128}");

    private final SsoProperties properties;
    private final ReplayNonceStore nonces;
    private final Clock clock;

    public HmacSsoAssertionVerifier(SsoProperties properties, ReplayNonceStore nonces, Clock clock) {
        this.properties = properties;
        this.nonces = nonces;
        this.clock = clock;
    }

    @Override
    public String verify(SsoLoginRequest assertion) {
        if (assertion == null
                || assertion.issuer() == null
                || !ISSUER_OR_AUDIENCE.matcher(assertion.issuer()).matches()
                || assertion.audience() == null
                || !ISSUER_OR_AUDIENCE.matcher(assertion.audience()).matches()
                || assertion.keyId() == null
                || !KEY_ID.matcher(assertion.keyId()).matches()
                || assertion.subject() == null
                || !SUBJECT.matcher(assertion.subject()).matches()
                || assertion.nonce() == null
                || !NONCE.matcher(assertion.nonce()).matches()) {
            throw invalid();
        }

        if (!assertion.issuer().equals(properties.expectedIssuer())
                || !assertion.audience().equals(properties.expectedAudience())
                || !assertion.keyId().equals(properties.activeKeyId())) {
            throw invalid();
        }

        Instant issuedAt = issuedAt(assertion.issuedAtEpochSeconds());
        Instant now = clock.instant();
        Duration maxAge = properties.maxAge();
        Duration futureSkew = properties.maxFutureSkew();

        if (issuedAt.isBefore(now.minus(maxAge)) || issuedAt.isAfter(now.plus(futureSkew))) {
            throw invalid();
        }

        byte[] supplied = decodeSignature(assertion.signature());
        byte[] expected = sign(assertion, properties.requiredSecretBytes());
        if (supplied.length != expected.length || !MessageDigest.isEqual(supplied, expected)) {
            throw invalid();
        }

        if (!nonces.markIfUnused(assertion.nonce(), now, maxAge.plus(futureSkew).plusSeconds(1))) {
            throw invalid();
        }
        return assertion.subject();
    }

    private static Instant issuedAt(long epochSeconds) {
        try {
            return Instant.ofEpochSecond(epochSeconds);
        } catch (DateTimeException invalidTime) {
            throw invalid();
        }
    }

    private static byte[] decodeSignature(String signature) {
        if (signature == null || signature.isBlank()) {
            throw invalid();
        }
        try {
            return Base64.getUrlDecoder().decode(signature);
        } catch (IllegalArgumentException invalidBase64) {
            throw invalid();
        }
    }

    private static byte[] sign(SsoLoginRequest assertion, byte[] secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(canonicalText(assertion).getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException unavailableAlgorithm) {
            throw new IllegalStateException("HmacSHA256 is unavailable", unavailableAlgorithm);
        }
    }

    static String canonicalText(SsoLoginRequest assertion) {
        return assertion.issuer() + "\n"
                + assertion.audience() + "\n"
                + assertion.keyId() + "\n"
                + assertion.subject() + "\n"
                + assertion.issuedAtEpochSeconds() + "\n"
                + assertion.nonce();
    }

    private static SsoAssertionException invalid() {
        return new SsoAssertionException("Invalid SSO assertion or account");
    }
}
