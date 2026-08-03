package io.github.son1004007.opsmate.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.AuditEvent;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

final class ApiModels {

    private ApiModels() {
    }

    record CreateDraftRequest(
            @NotBlank @Size(max = 2000) String requestText) {
    }

    record DecisionRequest(
            @NotNull ApprovalDecision decision,
            @Size(max = 500) String reason) {
    }

    record CreateOrderRequest(@NotNull UUID purchaseRequestId) {
    }

    record PurchaseRequestResponse(
            UUID id,
            long version,
            String requestText,
            String title,
            String purpose,
            BigDecimal amount,
            String currency,
            PurchaseCategory category,
            Set<String> policyEvidenceIds,
            PurchaseRequestStatus status,
            String requestedBy,
            String decidedBy,
            String rejectionReason,
            Instant createdAt,
            Instant updatedAt) {

        static PurchaseRequestResponse from(PurchaseRequest request) {
            return new PurchaseRequestResponse(
                    request.getId(),
                    request.getVersion(),
                    request.getRequestText(),
                    request.getTitle(),
                    request.getPurpose(),
                    request.getAmount(),
                    request.getCurrency(),
                    request.getCategory(),
                    request.getPolicyEvidenceIds(),
                    request.getStatus(),
                    request.getRequestedBy(),
                    request.getDecidedBy(),
                    request.getRejectionReason(),
                    request.getCreatedAt(),
                    request.getUpdatedAt());
        }
    }

    record PurchaseOrderResponse(
            UUID id,
            String orderNumber,
            UUID purchaseRequestId,
            String createdBy,
            Instant createdAt) {

        static PurchaseOrderResponse from(PurchaseOrder order) {
            return new PurchaseOrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getPurchaseRequestId(),
                    order.getCreatedBy(),
                    order.getCreatedAt());
        }
    }

    record AuditEventResponse(
            UUID id,
            Instant occurredAt,
            String aggregateType,
            UUID aggregateId,
            String actor,
            String action,
            String metadata) {

        static AuditEventResponse from(AuditEvent event) {
            return new AuditEventResponse(
                    event.getId(),
                    event.getOccurredAt(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    event.getActor(),
                    event.getAction(),
                    event.getMetadata());
        }
    }
}
