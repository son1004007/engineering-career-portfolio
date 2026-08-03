package io.github.son1004007.opsmate.application;

import java.util.UUID;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseOrder;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
        String actor = actorProvider.currentActor();
        String key = validateIdempotencyKey(idempotencyKey);
        String fingerprint = Fingerprints.sha256(purchaseRequestId.toString());

        PurchaseOrder existingForKey = repository.findByCreatedByAndIdempotencyKey(actor, key).orElse(null);
        if (existingForKey != null) {
            return requireSameFingerprint(existingForKey, fingerprint);
        }
        if (repository.findByPurchaseRequestId(purchaseRequestId).isPresent()) {
            throw duplicateOrder();
        }

        try {
            return transactions.persistOrder(purchaseRequestId, actor, key, fingerprint);
        } catch (DataIntegrityViolationException exception) {
            PurchaseOrder raced = repository.findByCreatedByAndIdempotencyKey(actor, key).orElse(null);
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
