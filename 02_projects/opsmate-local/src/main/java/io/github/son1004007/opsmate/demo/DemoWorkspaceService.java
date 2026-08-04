package io.github.son1004007.opsmate.demo;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.application.DraftGenerationCoordinator;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공개 방문자 workspace의 생성, 만료와 합성 데이터 삭제를 한 경계에서 관리한다.
 *
 * <p>종료 순서는 발주·감사·요청 row를 먼저 삭제한 뒤 workspace를 제거한다.
 * 데이터베이스 cascade가 있어도 애플리케이션 테스트에서 삭제 범위를 명시적으로
 * 확인할 수 있게 같은 순서를 유지한다.
 */
@Service
public class DemoWorkspaceService {

    private final DemoWorkspaceRepository workspaceRepository;
    private final PurchaseRequestRepository requestRepository;
    private final PurchaseOrderRepository orderRepository;
    private final AuditEventRepository auditRepository;
    private final DemoProperties properties;
    private final Clock clock;
    private final DraftGenerationCoordinator generationCoordinator;

    public DemoWorkspaceService(
            DemoWorkspaceRepository workspaceRepository,
            PurchaseRequestRepository requestRepository,
            PurchaseOrderRepository orderRepository,
            AuditEventRepository auditRepository,
            DemoProperties properties,
            Clock clock,
            DraftGenerationCoordinator generationCoordinator) {
        this.workspaceRepository = workspaceRepository;
        this.requestRepository = requestRepository;
        this.orderRepository = orderRepository;
        this.auditRepository = auditRepository;
        this.properties = properties;
        this.clock = clock;
        this.generationCoordinator = generationCoordinator;
        properties.validate();
    }

    @Transactional
    public DemoWorkspace start() {
        if (!properties.isEnabled() || !properties.isStartEnabled()) {
            throw new OpsMateException(ErrorCode.DEMO_CLOSED, "The public demo is currently closed");
        }
        Instant now = clock.instant();
        long active = workspaceRepository.countByStateAndExpiresAtAfter(DemoWorkspaceState.ACTIVE, now);
        if (active >= properties.getMaxActiveWorkspaces()) {
            throw new OpsMateException(
                    ErrorCode.DEMO_CAPACITY_REACHED,
                    "The public demo reached its active workspace limit");
        }
        return workspaceRepository.save(DemoWorkspace.active(now, properties.getWorkspaceTtl()));
    }

    @Transactional(readOnly = true)
    public DemoWorkspace requireActive(UUID workspaceId) {
        DemoWorkspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new OpsMateException(ErrorCode.SESSION_EXPIRED, "Demo workspace was not found"));
        if (!workspace.isActiveAt(clock.instant())) {
            throw new OpsMateException(ErrorCode.SESSION_EXPIRED, "The demo workspace has expired");
        }
        return workspace;
    }

    @Transactional
    public DemoWorkspace reset(UUID workspaceId) {
        closeInternal(workspaceId);
        return start();
    }

    @Transactional
    public void close(UUID workspaceId) {
        closeInternal(workspaceId);
    }

    @Transactional
    public int cleanupExpired() {
        List<DemoWorkspace> expired = workspaceRepository.findAllByExpiresAtLessThanEqual(clock.instant());
        expired.forEach(workspace -> closeInternal(workspace.getId()));
        return expired.size();
    }

    private void closeInternal(UUID workspaceId) {
        workspaceRepository.findById(workspaceId).ifPresent(DemoWorkspace::close);
        orderRepository.deleteAllByWorkspaceId(workspaceId);
        auditRepository.deleteAllByWorkspaceId(workspaceId);
        requestRepository.deleteAllByWorkspaceId(workspaceId);
        workspaceRepository.deleteById(workspaceId);
        generationCoordinator.forgetWorkspace(workspaceId);
    }
}
