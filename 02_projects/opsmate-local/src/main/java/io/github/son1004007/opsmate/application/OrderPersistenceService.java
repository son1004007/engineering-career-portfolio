package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 발주 생성, 구매 요청의 {@code ORDERED} 전이와 감사 이벤트를 원자적으로 저장한다.
 *
 * <p>후처리 hook까지 transaction 안에서 실행해 실패하면 발주 row, 상태와 성공
 * 이벤트가 모두 롤백된다. 실제 ERP adapter를 추가할 때는 장시간 외부 호출을 이
 * transaction에 넣지 않고 outbox 경계로 분리해야 한다.
 */
@Service
public class OrderPersistenceService {

    private final PurchaseRequestRepository requestRepository;
    private final PurchaseOrderRepository orderRepository;
    private final AuditRecorder auditRecorder;
    private final OrderPostPersistHook postPersistHook;
    private final Clock clock;
    private final WorkspaceWriteGuard workspaceWriteGuard;

    public OrderPersistenceService(
            PurchaseRequestRepository requestRepository,
            PurchaseOrderRepository orderRepository,
            AuditRecorder auditRecorder,
            OrderPostPersistHook postPersistHook,
            Clock clock,
            WorkspaceWriteGuard workspaceWriteGuard) {
        this.requestRepository = requestRepository;
        this.orderRepository = orderRepository;
        this.auditRecorder = auditRecorder;
        this.postPersistHook = postPersistHook;
        this.clock = clock;
        this.workspaceWriteGuard = workspaceWriteGuard;
    }

    /** 승인된 동일 workspace 요청 한 건에 발주 한 건만 생성한다. */
    @Transactional
    public PurchaseOrder persistOrder(
            UUID workspaceId,
            UUID purchaseRequestId,
            String actor,
            String idempotencyKey,
            String fingerprint) {
        Instant now = clock.instant();
        workspaceWriteGuard.requireActiveForWrite(workspaceId, now);
        orderRepository.findByWorkspaceIdAndPurchaseRequestId(workspaceId, purchaseRequestId).ifPresent(existing -> {
            throw new OpsMateException(ErrorCode.DUPLICATE_ORDER, "A purchase order already exists for this request");
        });

        PurchaseRequest request = requestRepository.findByIdAndWorkspaceId(purchaseRequestId, workspaceId)
                .orElseThrow(() -> new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found"));
        PurchaseOrder order = PurchaseOrder.create(
                workspaceId, purchaseRequestId, actor, idempotencyKey, fingerprint, now);
        orderRepository.saveAndFlush(order);
        request.markOrdered(now);
        auditRecorder.record(
                workspaceId,
                "PURCHASE_ORDER",
                order.getId(),
                actor,
                "ORDER_CREATED",
                "purchaseRequestId=" + purchaseRequestId);
        postPersistHook.afterPersist(order);
        return order;
    }
}
