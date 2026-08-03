package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.AuditEvent;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditRecorder {

    private final AuditEventRepository repository;
    private final Clock clock;

    public AuditRecorder(AuditEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void record(String aggregateType, UUID aggregateId, String actor, String action, String metadata) {
        repository.save(AuditEvent.of(clock.instant(), aggregateType, aggregateId, actor, action, metadata));
    }
}
