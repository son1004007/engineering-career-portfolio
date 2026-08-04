package io.github.son1004007.opsmate.application;

import java.util.List;

import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 현재 workspace의 합성 발주를 최근 순서로 최대 100건 조회한다. */
@Service
public class PurchaseOrderQueryService {

    private final PurchaseOrderRepository repository;
    private final ActorProvider actorProvider;

    public PurchaseOrderQueryService(PurchaseOrderRepository repository, ActorProvider actorProvider) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('BUYER', 'AUDITOR')")
    public List<PurchaseOrder> findVisible() {
        return repository.findTop100ByWorkspaceIdOrderByCreatedAtDesc(actorProvider.currentWorkspaceId());
    }
}
