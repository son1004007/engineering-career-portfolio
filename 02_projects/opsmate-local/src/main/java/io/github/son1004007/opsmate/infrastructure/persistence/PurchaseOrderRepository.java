package io.github.son1004007.opsmate.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByCreatedByAndIdempotencyKey(String createdBy, String idempotencyKey);

    Optional<PurchaseOrder> findByPurchaseRequestId(UUID purchaseRequestId);
}
