package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import io.github.son1004007.opsmate.agent.DraftAgentResult;
import io.github.son1004007.opsmate.agent.PolicyEvidence;
import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구매 초안·제출·결정과 대응 감사 이벤트를 같은 DB 트랜잭션에 저장한다.
 *
 * <p>모델 호출은 이 서비스 밖에서 이미 완료돼야 한다. 구조 검증을 통과하지 못한
 * 모델 출력이 transaction이나 entity 생성 단계에 들어오지 않도록 외부 I/O와
 * 결정적 DB 쓰기 경계를 분리한다.
 */
@Service
public class DraftPersistenceService {

    private final PurchaseRequestRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;
    private final WorkspaceWriteGuard workspaceWriteGuard;

    public DraftPersistenceService(
            PurchaseRequestRepository repository,
            AuditRecorder auditRecorder,
            Clock clock,
            WorkspaceWriteGuard workspaceWriteGuard) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
        this.workspaceWriteGuard = workspaceWriteGuard;
    }

    /** 초안과 {@code DRAFT_CREATED} 이벤트를 하나의 원자적 변경으로 저장한다. */
    @Transactional
    public PurchaseRequest persistDraft(
            UUID workspaceId,
            String actor,
            String idempotencyKey,
            String requestText,
            String fingerprint,
            DraftAgentResult result) {
        Instant now = clock.instant();
        workspaceWriteGuard.requireActiveForWrite(workspaceId, now);
        PurchaseRequest existing = repository.findByWorkspaceIdAndRequestedByAndIdempotencyKey(
                        workspaceId, actor, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            if (!existing.getRequestFingerprint().equals(fingerprint)) {
                throw new OpsMateException(
                        ErrorCode.IDEMPOTENCY_CONFLICT,
                        "Idempotency key was already used with different input");
            }
            return existing;
        }

        Set<String> policyIds = result.policyEvidence().stream()
                .map(PolicyEvidence::id)
                .filter(result.proposal().policyIds()::contains)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        PurchaseRequest request = PurchaseRequest.draft(
                workspaceId,
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
                workspaceId,
                "PURCHASE_REQUEST",
                request.getId(),
                actor,
                "DRAFT_CREATED",
                "category=" + request.getCategory() + ";policyCount=" + policyIds.size());
        return request;
    }

    @Transactional
    public PurchaseRequest submit(UUID workspaceId, UUID requestId, String actor) {
        Instant now = clock.instant();
        workspaceWriteGuard.requireActiveForWrite(workspaceId, now);
        PurchaseRequest request = requireRequest(workspaceId, requestId);
        request.submit(actor, now);
        auditRecorder.record(
                workspaceId, "PURCHASE_REQUEST", requestId, actor, "SUBMITTED", "result=PENDING_APPROVAL");
        return request;
    }

    @Transactional
    public PurchaseRequest decide(
            UUID workspaceId,
            UUID requestId,
            String actor,
            ApprovalDecision decision,
            String reason) {
        Instant now = clock.instant();
        workspaceWriteGuard.requireActiveForWrite(workspaceId, now);
        PurchaseRequest request = requireRequest(workspaceId, requestId);
        if (decision == ApprovalDecision.APPROVE) {
            request.approve(actor, now);
            auditRecorder.record(
                    workspaceId, "PURCHASE_REQUEST", requestId, actor, "APPROVED", "result=APPROVED");
        } else {
            request.reject(actor, reason, now);
            auditRecorder.record(
                    workspaceId, "PURCHASE_REQUEST", requestId, actor, "REJECTED", "result=REJECTED");
        }
        return request;
    }

    private PurchaseRequest requireRequest(UUID workspaceId, UUID requestId) {
        return repository.findByIdAndWorkspaceId(requestId, workspaceId)
                .orElseThrow(() -> new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found"));
    }
}
