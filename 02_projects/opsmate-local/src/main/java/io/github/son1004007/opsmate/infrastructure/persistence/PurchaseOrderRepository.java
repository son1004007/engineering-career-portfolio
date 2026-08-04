package io.github.son1004007.opsmate.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByWorkspaceIdAndCreatedByAndIdempotencyKey(
            UUID workspaceId,
            String createdBy,
            String idempotencyKey);

    Optional<PurchaseOrder> findByWorkspaceIdAndPurchaseRequestId(UUID workspaceId, UUID purchaseRequestId);

    List<PurchaseOrder> findTop100ByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    long countByWorkspaceId(UUID workspaceId);

    void deleteAllByWorkspaceId(UUID workspaceId);
}
