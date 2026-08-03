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

@Service
public class OrderPersistenceService {

    private final PurchaseRequestRepository requestRepository;
    private final PurchaseOrderRepository orderRepository;
    private final AuditRecorder auditRecorder;
    private final OrderPostPersistHook postPersistHook;
    private final Clock clock;

    public OrderPersistenceService(
            PurchaseRequestRepository requestRepository,
            PurchaseOrderRepository orderRepository,
            AuditRecorder auditRecorder,
            OrderPostPersistHook postPersistHook,
            Clock clock) {
        this.requestRepository = requestRepository;
        this.orderRepository = orderRepository;
        this.auditRecorder = auditRecorder;
        this.postPersistHook = postPersistHook;
        this.clock = clock;
    }

    @Transactional
    public PurchaseOrder persistOrder(
            UUID purchaseRequestId,
            String actor,
            String idempotencyKey,
            String fingerprint) {
        orderRepository.findByPurchaseRequestId(purchaseRequestId).ifPresent(existing -> {
            throw new OpsMateException(ErrorCode.DUPLICATE_ORDER, "A purchase order already exists for this request");
        });

        PurchaseRequest request = requestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found"));
        Instant now = clock.instant();
        PurchaseOrder order = PurchaseOrder.create(purchaseRequestId, actor, idempotencyKey, fingerprint, now);
        orderRepository.saveAndFlush(order);
        request.markOrdered(now);
        auditRecorder.record(
                "PURCHASE_ORDER",
                order.getId(),
                actor,
                "ORDER_CREATED",
                "purchaseRequestId=" + purchaseRequestId);
        postPersistHook.afterPersist(order);
        return order;
    }
}
