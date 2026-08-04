package io.github.son1004007.opsmate.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * 구매 초안부터 발주 완료까지의 허용 상태 전이를 소유하는 aggregate.
 *
 * <p>LLM 출력은 이 객체를 직접 변경하지 못한다. 인증된 application service가
 * workspace, 역할과 입력을 확인한 뒤 이 메서드를 호출하며, 도메인 객체가 마지막으로
 * 현재 상태와 자기 승인 금지 규칙을 검사한다.
 */
@Entity
@Table(
        name = "purchase_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_purchase_request_actor_idempotency",
                        columnNames = {"workspace_id", "requested_by", "idempotency_key"}),
                @UniqueConstraint(
                        name = "uk_purchase_request_workspace_id",
                        columnNames = {"workspace_id", "id"})
        },
        indexes = @Index(
                name = "idx_purchase_request_workspace_status_updated",
                columnList = "workspace_id,status,updated_at"))
public class PurchaseRequest {

    @Id
    private UUID id;

    @Version
    private long version;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "request_text", nullable = false, length = 4000)
    private String requestText;

    @Column(name = "request_fingerprint", nullable = false, length = 64)
    private String requestFingerprint;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String purpose;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PurchaseCategory category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "purchase_request_policy_evidence",
            joinColumns = @JoinColumn(name = "purchase_request_id"))
    @Column(name = "policy_id", nullable = false, length = 80)
    private Set<String> policyEvidenceIds = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PurchaseRequestStatus status;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "decided_by", length = 100)
    private String decidedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PurchaseRequest() {
    }

    public static PurchaseRequest draft(
            UUID workspaceId,
            String requestText,
            String requestFingerprint,
            String title,
            String purpose,
            BigDecimal amount,
            String currency,
            PurchaseCategory category,
            Set<String> policyEvidenceIds,
            String requestedBy,
            String idempotencyKey,
            Instant now) {
        PurchaseRequest request = new PurchaseRequest();
        request.id = UUID.randomUUID();
        request.workspaceId = workspaceId;
        request.requestText = requestText;
        request.requestFingerprint = requestFingerprint;
        request.title = title;
        request.purpose = purpose;
        request.amount = amount;
        request.currency = currency;
        request.category = category;
        request.policyEvidenceIds.addAll(policyEvidenceIds);
        request.status = PurchaseRequestStatus.DRAFT;
        request.requestedBy = requestedBy;
        request.idempotencyKey = idempotencyKey;
        request.createdAt = now;
        request.updatedAt = now;
        return request;
    }

    public void submit(String actor, Instant now) {
        requireOwner(actor);
        requireStatus(PurchaseRequestStatus.DRAFT);
        status = PurchaseRequestStatus.PENDING_APPROVAL;
        updatedAt = now;
    }

    public void approve(String actor, Instant now) {
        requireStatus(PurchaseRequestStatus.PENDING_APPROVAL);
        if (requestedBy.equals(actor)) {
            throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "Self-approval is not allowed");
        }
        decidedBy = actor;
        rejectionReason = null;
        status = PurchaseRequestStatus.APPROVED;
        updatedAt = now;
    }

    public void reject(String actor, String reason, Instant now) {
        requireStatus(PurchaseRequestStatus.PENDING_APPROVAL);
        if (requestedBy.equals(actor)) {
            throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "Self-rejection is not allowed");
        }
        String normalizedReason = reason == null ? null : reason.strip();
        if (normalizedReason == null || normalizedReason.isEmpty()) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Rejection reason is required");
        }
        // REST·MVC adapter가 달라도 DB column 길이 전에 같은 도메인 규칙으로 거부한다.
        if (normalizedReason.length() > 500) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Rejection reason must not exceed 500 characters");
        }
        decidedBy = actor;
        rejectionReason = normalizedReason;
        status = PurchaseRequestStatus.REJECTED;
        updatedAt = now;
    }

    public void markOrdered(Instant now) {
        requireStatus(PurchaseRequestStatus.APPROVED);
        status = PurchaseRequestStatus.ORDERED;
        updatedAt = now;
    }

    private void requireOwner(String actor) {
        if (!requestedBy.equals(actor)) {
            throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "Only the requester can submit this draft");
        }
    }

    private void requireStatus(PurchaseRequestStatus expected) {
        if (status != expected) {
            throw new OpsMateException(
                    ErrorCode.INVALID_STATE,
                    "Expected status " + expected + " but was " + status);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public long getVersion() {
        return version;
    }

    public String getRequestText() {
        return requestText;
    }

    public String getRequestFingerprint() {
        return requestFingerprint;
    }

    public String getTitle() {
        return title;
    }

    public String getPurpose() {
        return purpose;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PurchaseCategory getCategory() {
        return category;
    }

    public Set<String> getPolicyEvidenceIds() {
        return Set.copyOf(policyEvidenceIds);
    }

    public PurchaseRequestStatus getStatus() {
        return status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
