package io.github.son1004007.opsmate.application;

import java.util.UUID;

import io.github.son1004007.opsmate.agent.DraftAgentResult;
import io.github.son1004007.opsmate.agent.PurchaseDraftAgent;
import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository repository;
    private final PurchaseDraftAgent agent;
    private final DraftPersistenceService transactions;
    private final ActorProvider actorProvider;
    private final PurchaseRequestReadPolicy readPolicy;
    private final DraftGenerationCoordinator coordinator;

    public PurchaseRequestService(
            PurchaseRequestRepository repository,
            PurchaseDraftAgent agent,
            DraftPersistenceService transactions,
            ActorProvider actorProvider,
            PurchaseRequestReadPolicy readPolicy,
            DraftGenerationCoordinator coordinator) {
        this.repository = repository;
        this.agent = agent;
        this.transactions = transactions;
        this.actorProvider = actorProvider;
        this.readPolicy = readPolicy;
        this.coordinator = coordinator;
    }

    /**
     * 동일 workspace·actor·멱등키의 재시도를 하나의 구매 초안으로 수렴시킨다.
     *
     * <p>모델 호출 전에 기존 저장 결과를 확인하고, 최초 동시 요청은 single-flight로
     * 한 번만 생성한다. DB 고유 제약은 프로세스 경계를 넘는 최종 판정자이며, 같은
     * 키에 다른 입력이 들어오면 기존 결과를 반환하지 않고 충돌로 중단한다.
     */
    @PreAuthorize("hasRole('REQUESTER')")
    public PurchaseRequest createDraft(String idempotencyKey, String rawRequestText) {
        ActorContext context = actorProvider.currentContext();
        String key = validateIdempotencyKey(idempotencyKey);
        String requestText = validateRequestText(rawRequestText);
        String fingerprint = Fingerprints.sha256(requestText);

        PurchaseRequest existing = repository.findByWorkspaceIdAndRequestedByAndIdempotencyKey(
                context.workspaceId(), context.actor(), key).orElse(null);
        if (existing != null) {
            return requireSameFingerprint(existing, fingerprint);
        }

        PurchaseRequest request = coordinator.execute(
                context.workspaceId(),
                context.actor(),
                key,
                () -> createDraftOnce(context, key, requestText, fingerprint));
        return requireSameFingerprint(request, fingerprint);
    }

    private PurchaseRequest createDraftOnce(
            ActorContext context,
            String key,
            String requestText,
            String fingerprint) {
        PurchaseRequest existing = repository.findByWorkspaceIdAndRequestedByAndIdempotencyKey(
                context.workspaceId(), context.actor(), key).orElse(null);
        if (existing != null) {
            return requireSameFingerprint(existing, fingerprint);
        }

        DraftAgentResult result = agent.createDraft(requestText);
        try {
            return transactions.persistDraft(
                    context.workspaceId(), context.actor(), key, requestText, fingerprint, result);
        } catch (DataIntegrityViolationException exception) {
            // 애플리케이션 선조회만으로는 DB 경합을 제거할 수 없으므로,
            // 유니크 제약 충돌 뒤 승자 row를 다시 읽어 입력이 같은지 확인한다.
            PurchaseRequest raced = repository.findByWorkspaceIdAndRequestedByAndIdempotencyKey(
                            context.workspaceId(), context.actor(), key)
                    .orElseThrow(() -> exception);
            return requireSameFingerprint(raced, fingerprint);
        }
    }

    @PreAuthorize("hasRole('REQUESTER')")
    public PurchaseRequest submit(UUID requestId) {
        ActorContext context = actorProvider.currentContext();
        return transactions.submit(context.workspaceId(), requestId, context.actor());
    }

    @PreAuthorize("hasRole('APPROVER')")
    public PurchaseRequest decide(UUID requestId, ApprovalDecision decision, String reason) {
        if (decision == null) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Decision is required");
        }
        ActorContext context = actorProvider.currentContext();
        return transactions.decide(context.workspaceId(), requestId, context.actor(), decision, reason);
    }

    @PreAuthorize("hasAnyRole('REQUESTER', 'APPROVER', 'BUYER', 'AUDITOR')")
    public PurchaseRequest get(UUID requestId) {
        ActorContext context = actorProvider.currentContext();
        PurchaseRequest request = repository.findByIdAndWorkspaceId(requestId, context.workspaceId()).orElse(null);
        if (request == null) {
            if (readPolicy.canDistinguishNotFound(context.authentication())) {
                throw new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found");
            }
            throw new OpsMateException(
                    ErrorCode.UNAUTHORIZED_ACTION,
                    "The purchase request is not readable in this role and state");
        }
        readPolicy.requireReadable(request, context);
        return request;
    }

    private PurchaseRequest requireSameFingerprint(PurchaseRequest request, String fingerprint) {
        if (!request.getRequestFingerprint().equals(fingerprint)) {
            throw new OpsMateException(
                    ErrorCode.IDEMPOTENCY_CONFLICT,
                    "Idempotency key was already used with different input");
        }
        return request;
    }

    private String validateIdempotencyKey(String rawKey) {
        if (rawKey == null || !rawKey.matches("[A-Za-z0-9._:-]{8,100}")) {
            throw new OpsMateException(
                    ErrorCode.VALIDATION_ERROR,
                    "Idempotency-Key must be 8-100 safe ASCII characters");
        }
        return rawKey;
    }

    private String validateRequestText(String rawText) {
        if (rawText == null || rawText.isBlank() || rawText.length() > 2000) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Request text must be 1-2000 characters");
        }
        return rawText.strip();
    }
}
