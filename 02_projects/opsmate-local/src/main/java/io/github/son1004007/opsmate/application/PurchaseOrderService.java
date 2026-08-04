package io.github.son1004007.opsmate.application;

import java.util.UUID;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * 승인된 요청을 멱등한 합성 발주로 전환하는 application service.
 *
 * <p>사전 조회는 빠른 응답을 위한 경로이고, 동시 요청의 최종 중복 판정은
 * workspace를 포함한 DB 고유 제약이 수행한다. 같은 멱등키의 입력 fingerprint가
 * 다르면 기존 발주를 반환하지 않는다.
 */
@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;
    private final OrderPersistenceService transactions;
    private final ActorProvider actorProvider;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            OrderPersistenceService transactions,
            ActorProvider actorProvider) {
        this.repository = repository;
        this.transactions = transactions;
        this.actorProvider = actorProvider;
    }

    @PreAuthorize("hasRole('BUYER')")
    public PurchaseOrder createOrder(UUID purchaseRequestId, String idempotencyKey) {
        ActorContext context = actorProvider.currentContext();
        String key = validateIdempotencyKey(idempotencyKey);
        String fingerprint = Fingerprints.sha256(purchaseRequestId.toString());

        PurchaseOrder existingForKey = repository.findByWorkspaceIdAndCreatedByAndIdempotencyKey(
                context.workspaceId(), context.actor(), key).orElse(null);
        if (existingForKey != null) {
            return requireSameFingerprint(existingForKey, fingerprint);
        }
        if (repository.findByWorkspaceIdAndPurchaseRequestId(context.workspaceId(), purchaseRequestId).isPresent()) {
            throw duplicateOrder();
        }

        try {
            return transactions.persistOrder(
                    context.workspaceId(), purchaseRequestId, context.actor(), key, fingerprint);
        } catch (DataIntegrityViolationException exception) {
            PurchaseOrder raced = repository.findByWorkspaceIdAndCreatedByAndIdempotencyKey(
                    context.workspaceId(), context.actor(), key).orElse(null);
            if (raced != null) {
                return requireSameFingerprint(raced, fingerprint);
            }
            throw duplicateOrder();
        }
    }

    private PurchaseOrder requireSameFingerprint(PurchaseOrder order, String fingerprint) {
        if (!order.getRequestFingerprint().equals(fingerprint)) {
            throw new OpsMateException(
                    ErrorCode.IDEMPOTENCY_CONFLICT,
                    "Idempotency key was already used with a different purchase request");
        }
        return order;
    }

    private String validateIdempotencyKey(String rawKey) {
        if (rawKey == null || !rawKey.matches("[A-Za-z0-9._:-]{8,100}")) {
            throw new OpsMateException(
                    ErrorCode.VALIDATION_ERROR,
                    "Idempotency-Key must be 8-100 safe ASCII characters");
        }
        return rawKey;
    }

    private OpsMateException duplicateOrder() {
        return new OpsMateException(ErrorCode.DUPLICATE_ORDER, "A purchase order already exists for this request");
    }
}
