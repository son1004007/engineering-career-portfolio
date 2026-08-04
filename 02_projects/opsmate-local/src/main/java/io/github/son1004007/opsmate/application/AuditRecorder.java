package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.AuditEvent;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import org.springframework.stereotype.Service;

/**
 * 성공한 업무 상태 변경을 호출 서비스의 transaction 안에서 workspace별 감사 이벤트로 기록한다.
 *
 * <p>별도 비동기 저장을 하지 않으므로 업무 row만 남거나 감사 row만 남는 부분 성공을 피한다.
 * metadata에는 합성 식별 정보만 넣고 자연어 원문·token·credential은 기록하지 않는다.
 */
@Service
public class AuditRecorder {

    private final AuditEventRepository repository;
    private final Clock clock;

    public AuditRecorder(AuditEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void record(
            UUID workspaceId,
            String aggregateType,
            UUID aggregateId,
            String actor,
            String action,
            String metadata) {
        repository.save(AuditEvent.of(
                workspaceId,
                clock.instant(),
                aggregateType,
                aggregateId,
                actor,
                action,
                metadata));
    }
}
