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

    public PurchaseRequestService(
            PurchaseRequestRepository repository,
            PurchaseDraftAgent agent,
            DraftPersistenceService transactions,
            ActorProvider actorProvider,
            PurchaseRequestReadPolicy readPolicy) {
        this.repository = repository;
        this.agent = agent;
        this.transactions = transactions;
        this.actorProvider = actorProvider;
        this.readPolicy = readPolicy;
    }

    @PreAuthorize("hasRole('REQUESTER')")
    public PurchaseRequest createDraft(String idempotencyKey, String rawRequestText) {
        String actor = actorProvider.currentActor();
        String key = validateIdempotencyKey(idempotencyKey);
        String requestText = validateRequestText(rawRequestText);
        String fingerprint = Fingerprints.sha256(requestText);

        PurchaseRequest existing = repository.findByRequestedByAndIdempotencyKey(actor, key).orElse(null);
        if (existing != null) {
            return requireSameFingerprint(existing, fingerprint);
        }

        DraftAgentResult result = agent.createDraft(requestText);
        try {
            return transactions.persistDraft(actor, key, requestText, fingerprint, result);
        } catch (DataIntegrityViolationException exception) {
            PurchaseRequest raced = repository.findByRequestedByAndIdempotencyKey(actor, key)
                    .orElseThrow(() -> exception);
            return requireSameFingerprint(raced, fingerprint);
        }
    }

    @PreAuthorize("hasRole('REQUESTER')")
    public PurchaseRequest submit(UUID requestId) {
        return transactions.submit(requestId, actorProvider.currentActor());
    }

    @PreAuthorize("hasRole('APPROVER')")
    public PurchaseRequest decide(UUID requestId, ApprovalDecision decision, String reason) {
        if (decision == null) {
            throw new OpsMateException(ErrorCode.VALIDATION_ERROR, "Decision is required");
        }
        return transactions.decide(requestId, actorProvider.currentActor(), decision, reason);
    }

    @PreAuthorize("hasAnyRole('REQUESTER', 'APPROVER', 'BUYER', 'AUDITOR')")
    public PurchaseRequest get(UUID requestId) {
        var authentication = actorProvider.currentAuthentication();
        PurchaseRequest request = repository.findById(requestId).orElse(null);
        if (request == null) {
            if (readPolicy.canDistinguishNotFound(authentication)) {
                throw new OpsMateException(ErrorCode.NOT_FOUND, "Purchase request was not found");
            }
            throw new OpsMateException(
                    ErrorCode.UNAUTHORIZED_ACTION,
                    "The purchase request is not readable in this role and state");
        }
        readPolicy.requireReadable(request, authentication);
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
