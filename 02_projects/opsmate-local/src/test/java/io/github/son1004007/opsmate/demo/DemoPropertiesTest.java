package io.github.son1004007.opsmate.demo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.junit.jupiter.api.Test;

class DemoPropertiesTest {

    @Test
    void nonPositiveWorkspaceTtlIsRejected() {
        DemoProperties properties = new DemoProperties();
        properties.setWorkspaceTtl(Duration.ZERO);

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void nonPositiveCleanupIntervalIsRejected() {
        DemoProperties properties = new DemoProperties();
        properties.setCleanupInterval(Duration.ofSeconds(-1));

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void globalAdmissionLimitCannotBeResetByCreatingAnotherWorkspace() {
        DemoProperties properties = new DemoProperties();
        properties.setMaxStartsGlobal(2);
        DemoAdmissionLimiter limiter = new DemoAdmissionLimiter(
                properties,
                Clock.fixed(Instant.parse("2026-08-04T00:00:00Z"), ZoneOffset.UTC));

        limiter.acquire();
        limiter.acquire();

        assertThatThrownBy(limiter::acquire)
                .isInstanceOf(OpsMateException.class)
                .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                        .isEqualTo(ErrorCode.RATE_LIMITED));
    }
}
