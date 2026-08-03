package io.github.son1004007.opsmate.application;

import java.util.List;

import io.github.son1004007.opsmate.domain.AuditEvent;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditQueryService {

    private final AuditEventRepository repository;

    public AuditQueryService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('AUDITOR')")
    public List<AuditEvent> findAll() {
        return repository.findAllByOrderByOccurredAtAsc();
    }
}
