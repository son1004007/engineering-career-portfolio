package com.songisuk.portfolio.authbridge.sso;

import com.songisuk.portfolio.authbridge.config.SsoProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HmacSsoAssertionVerifierTest {

    private static final Instant NOW = Instant.parse("2026-08-03T12:00:00Z");
    private static final String SECRET = "test-only-sso-shared-secret-that-is-long-enough-01";
    private static final String ISSUER = "portfolio-demo-idp";
    private static final String AUDIENCE = "spring-security-auth-bridge";
    private static final String KEY_ID = "demo-key-v1";

    private SsoProperties properties;
    private HmacSsoAssertionVerifier verifier;

    @BeforeEach
    void setUp() {
        properties = new SsoProperties();
        properties.setSharedSecret(SECRET);
        properties.setExpectedIssuer(ISSUER);
        properties.setExpectedAudience(AUDIENCE);
        properties.setActiveKeyId(KEY_ID);
        properties.setMaxAge(Duration.ofMinutes(2));
        properties.setMaxFutureSkew(Duration.ofSeconds(10));
        verifier = new HmacSsoAssertionVerifier(properties, new InMemoryReplayNonceStore(),
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void acceptsBothTimeWindowBoundaries() throws Exception {
        SsoLoginRequest oldest = signed("subject-oldest", NOW.minusSeconds(120), "nonce-oldest-0001");
        SsoLoginRequest newest = signed("subject-newest", NOW.plusSeconds(10), "nonce-newest-0001");

        assertThat(verifier.verify(oldest)).isEqualTo("subject-oldest");
        assertThat(verifier.verify(newest)).isEqualTo("subject-newest");
    }

    @Test
    void rejectsTimesOutsideEitherBoundary() throws Exception {
        SsoLoginRequest tooOld = signed("subject-old", NOW.minusSeconds(121), "nonce-too-old-001");
        SsoLoginRequest tooNew = signed("subject-new", NOW.plusSeconds(11), "nonce-too-new-001");

        assertThatThrownBy(() -> verifier.verify(tooOld)).isInstanceOf(SsoAssertionException.class);
        assertThatThrownBy(() -> verifier.verify(tooNew)).isInstanceOf(SsoAssertionException.class);
    }

    @Test
    void rejectsTheSameNonceAfterSuccessfulVerification() throws Exception {
        SsoLoginRequest assertion = signed("subject-replay", NOW, "nonce-replay-0001");

        assertThat(verifier.verify(assertion)).isEqualTo("subject-replay");
        assertThatThrownBy(() -> verifier.verify(assertion)).isInstanceOf(SsoAssertionException.class);
    }

    @Test
    void keepsNonceUntilAFutureSkewAssertionIsNoLongerAccepted() throws Exception {
        AdjustableClock adjustableClock = new AdjustableClock(NOW);
        HmacSsoAssertionVerifier adjustableVerifier = new HmacSsoAssertionVerifier(
                properties, new InMemoryReplayNonceStore(), adjustableClock);
        SsoLoginRequest assertion = signed(
                "subject-future-replay", NOW.plusSeconds(10), "nonce-future-edge1");

        assertThat(adjustableVerifier.verify(assertion)).isEqualTo("subject-future-replay");
        adjustableClock.setInstant(NOW.plusSeconds(130));
        assertThatThrownBy(() -> adjustableVerifier.verify(assertion))
                .isInstanceOf(SsoAssertionException.class);
    }

    @Test
    void rejectsSubjectContainingCanonicalTextDelimiter() throws Exception {
        SsoLoginRequest assertion = signed("subject\nadmin", NOW, "nonce-delimiter-01");

        assertThatThrownBy(() -> verifier.verify(assertion)).isInstanceOf(SsoAssertionException.class);
    }

    @Test
    void rejectsIssuerAudienceAndKeyIdFromAnotherRelyingPartyOrKey() throws Exception {
        SsoLoginRequest wrongIssuer = signed(
                "another-idp", AUDIENCE, KEY_ID, "subject-issuer", NOW, "nonce-wrong-issuer1");
        SsoLoginRequest wrongAudience = signed(
                ISSUER, "another-service", KEY_ID, "subject-audience", NOW, "nonce-wrong-aud-001");
        SsoLoginRequest wrongKey = signed(
                ISSUER, AUDIENCE, "retired-key-v0", "subject-key", NOW, "nonce-wrong-key-001");

        assertThatThrownBy(() -> verifier.verify(wrongIssuer)).isInstanceOf(SsoAssertionException.class);
        assertThatThrownBy(() -> verifier.verify(wrongAudience)).isInstanceOf(SsoAssertionException.class);
        assertThatThrownBy(() -> verifier.verify(wrongKey)).isInstanceOf(SsoAssertionException.class);
    }

    @Test
    void missingOrWeakSharedSecretFailsClosed() {
        SsoProperties missing = new SsoProperties();
        missing.setExpectedIssuer(ISSUER);
        missing.setExpectedAudience(AUDIENCE);
        missing.setActiveKeyId(KEY_ID);
        missing.setMaxAge(Duration.ofMinutes(2));
        missing.setMaxFutureSkew(Duration.ofSeconds(10));
        HmacSsoAssertionVerifier missingSecretVerifier = new HmacSsoAssertionVerifier(
                missing, new InMemoryReplayNonceStore(), Clock.fixed(NOW, ZoneOffset.UTC));
        SsoLoginRequest assertion = new SsoLoginRequest(
                ISSUER, AUDIENCE, KEY_ID, "subject-config", NOW.getEpochSecond(), "nonce-config-0001",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        assertThatThrownBy(() -> missingSecretVerifier.verify(assertion))
                .isInstanceOf(SsoAdapterUnavailableException.class);

        missing.setSharedSecret("too-short");
        assertThatThrownBy(() -> missingSecretVerifier.verify(assertion))
                .isInstanceOf(SsoAdapterUnavailableException.class);
    }

    private static SsoLoginRequest signed(String subject, Instant issuedAt, String nonce) throws Exception {
        return signed(ISSUER, AUDIENCE, KEY_ID, subject, issuedAt, nonce);
    }

    private static SsoLoginRequest signed(
            String issuer,
            String audience,
            String keyId,
            String subject,
            Instant issuedAt,
            String nonce
    ) throws Exception {
        SsoLoginRequest unsigned = new SsoLoginRequest(
                issuer, audience, keyId, subject, issuedAt.getEpochSecond(), nonce, "pending");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                mac.doFinal(HmacSsoAssertionVerifier.canonicalText(unsigned).getBytes(StandardCharsets.UTF_8)));
        return new SsoLoginRequest(issuer, audience, keyId, subject,
                issuedAt.getEpochSecond(), nonce, signature);
    }

    private static final class AdjustableClock extends Clock {

        private Instant instant;

        private AdjustableClock(Instant instant) {
            this.instant = instant;
        }

        void setInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            if (!ZoneOffset.UTC.equals(zone)) {
                throw new IllegalArgumentException("This test clock only supports UTC");
            }
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
