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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(
        name = "purchase_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_purchase_request_actor_idempotency",
                columnNames = {"requested_by", "idempotency_key"}))
public class PurchaseRequest {

    @Id
    private UUID id;

    @Version
    private long version;

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
        if (reason == null || reason.isBlank()) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Rejection reason is required");
        }
        decidedBy = actor;
        rejectionReason = reason.strip();
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
