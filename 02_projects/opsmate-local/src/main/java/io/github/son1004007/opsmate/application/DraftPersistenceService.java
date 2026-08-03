package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

import io.github.son1004007.opsmate.agent.DraftAgentResult;
import io.github.son1004007.opsmate.agent.PolicyEvidence;
import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DraftPersistenceService {

    private final PurchaseRequestRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    public DraftPersistenceService(
            PurchaseRequestRepository repository,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    @Transactional
    public PurchaseRequest persistDraft(
            String actor,
            String idempotencyKey,
            String requestText,
            String fingerprint,
            DraftAgentResult result) {
        PurchaseRequest existing = repository.findByRequestedByAndIdempotencyKey(actor, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            if (!existing.getRequestFingerprint().equals(fingerprint)) {
                throw new OpsMateException(
                        ErrorCode.IDEMPOTENCY_CONFLICT,
                        "Idempotency key was already used with different input");
            }
            return existing;
        }

        Instant now = clock.instant();
        Set<String> policyIds = result.policyEvidence().stream()
                .map(PolicyEvidence::id)
                .filter(result.proposal().policyIds()::contains)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        PurchaseRequest request = PurchaseRequest.draft(
                requestText,
                fingerprint,
                result.proposal().title().strip(),
                result.proposal().purpose().strip(),
                result.proposal().amount(),
                result.proposal().currency(),
                result.proposal().category(),
                policyIds,
                actor,
                idempotencyKey,
                now);
        repository.saveAndFlush(request);
        auditRecorder.record(
                "PURCHASE_REQUEST",
                request.getId(),
                actor,
                "DRAFT_CREATED",
                "category=" + request.getCategory() + ";policyCount=" + policyIds.size());
        return request;
    }

    @Transactional
    public PurchaseRequest submit(java.util.UUID requestId, String actor) {
        PurchaseRequest request = requireRequest(requestId);
        request.submit(actor, clock.instant());
        auditRecorder.record("PURCHASE_REQUEST", requestId, actor, "SUBMITTED", "result=PENDING_APPROVAL");
        return request;
    }

    @Transactional
    public PurchaseRequest decide(
            java.util.UUID requestId,
            String actor,
            ApprovalDecision decision,
            String reason) {
        PurchaseRequest request = requireRequest(requestId);
        if (decision == ApprovalDecision.APPROVE) {
            request.approve(actor, clock.instant());
            auditRecorder.record("PURCHASE_REQUEST", requestId, actor, "APPROVED", "result=APPROVED");
        } else {
            request.reject(actor, reason, clock.instant());
            auditRecorder.record("PURCHASE_REQUEST", requestId, actor, "REJECTED", "result=REJECTED");
        }
        return request;
    }

    private PurchaseRequest requireRequest(java.util.UUID requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found"));
    }
}
