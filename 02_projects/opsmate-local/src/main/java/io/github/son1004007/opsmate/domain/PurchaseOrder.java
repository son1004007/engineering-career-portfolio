package io.github.son1004007.opsmate.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 승인된 구매 요청 한 건에서만 생성되는 합성 발주 aggregate.
 *
 * <p>application 선조회는 빠른 오류 응답을 위한 것이며, 요청당 한 건과
 * 멱등키 불변식은 workspace를 포함한 DB 고유 제약이 최종 판정한다.
 */
@Entity
@Table(
        name = "purchase_orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_purchase_order_request",
                        columnNames = {"workspace_id", "purchase_request_id"}),
                @UniqueConstraint(
                        name = "uk_purchase_order_actor_idempotency",
                        columnNames = {"workspace_id", "created_by", "idempotency_key"})
        },
        indexes = @Index(
                name = "idx_purchase_order_workspace_created",
                columnList = "workspace_id,created_at"))
public class PurchaseOrder {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;

    @Column(name = "purchase_request_id", nullable = false)
    private UUID purchaseRequestId;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_fingerprint", nullable = false, length = 64)
    private String requestFingerprint;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PurchaseOrder() {
    }

    public static PurchaseOrder create(
            UUID workspaceId,
            UUID purchaseRequestId,
            String createdBy,
            String idempotencyKey,
            String requestFingerprint,
            Instant now) {
        PurchaseOrder order = new PurchaseOrder();
        order.id = UUID.randomUUID();
        order.workspaceId = workspaceId;
        order.orderNumber = "PO-" + order.id.toString().substring(0, 8).toUpperCase();
        order.purchaseRequestId = purchaseRequestId;
        order.createdBy = createdBy;
        order.idempotencyKey = idempotencyKey;
        order.requestFingerprint = requestFingerprint;
        order.createdAt = now;
        return order;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public UUID getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getRequestFingerprint() {
        return requestFingerprint;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
