package io.github.son1004007.opsmate.demo;

import java.time.Clock;
import java.time.Instant;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.stereotype.Component;

/**
 * session 종료·cookie 삭제를 반복해도 공개 workspace 생성 비용을 무한 소비하지 못하게 한다.
 *
 * <p>IP나 개인정보를 저장하지 않는 process 전체 fixed window다. 단일 인스턴스 전용이며
 * 수평 확장 시에는 edge 또는 분산 rate limiter로 교체해야 한다.
 */
@Component
public class DemoAdmissionLimiter {

    private final DemoProperties properties;
    private final Clock clock;
    private Instant windowEndsAt;
    private int count;

    public DemoAdmissionLimiter(DemoProperties properties, Clock clock) {
        properties.validate();
        this.properties = properties;
        this.clock = clock;
        this.windowEndsAt = clock.instant().plus(properties.getAdmissionWindow());
    }

    public synchronized void acquire() {
        Instant now = clock.instant();
        if (!now.isBefore(windowEndsAt)) {
            windowEndsAt = now.plus(properties.getAdmissionWindow());
            count = 0;
        }
        if (count >= properties.getMaxStartsGlobal()) {
            throw new OpsMateException(ErrorCode.RATE_LIMITED, "The public demo reached its session start limit");
        }
        count++;
    }
}
