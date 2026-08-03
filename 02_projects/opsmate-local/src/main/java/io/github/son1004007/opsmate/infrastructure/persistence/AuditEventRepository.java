package io.github.son1004007.opsmate.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findAllByOrderByOccurredAtAsc();

    long countByAction(String action);
}
