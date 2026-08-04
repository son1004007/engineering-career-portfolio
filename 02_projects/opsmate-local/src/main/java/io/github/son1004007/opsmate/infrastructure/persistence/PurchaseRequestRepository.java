package io.github.son1004007.opsmate.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {

    Optional<PurchaseRequest> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    Optional<PurchaseRequest> findByWorkspaceIdAndRequestedByAndIdempotencyKey(
            UUID workspaceId,
            String requestedBy,
            String idempotencyKey);

    List<PurchaseRequest> findTop100ByWorkspaceIdAndRequestedByOrderByUpdatedAtDesc(
            UUID workspaceId,
            String requestedBy);

    List<PurchaseRequest> findTop100ByWorkspaceIdAndStatusOrderByUpdatedAtDesc(
            UUID workspaceId,
            PurchaseRequestStatus status);

    List<PurchaseRequest> findTop100ByWorkspaceIdAndStatusInOrderByUpdatedAtDesc(
            UUID workspaceId,
            Collection<PurchaseRequestStatus> statuses);

    List<PurchaseRequest> findTop100ByWorkspaceIdOrderByUpdatedAtDesc(UUID workspaceId);

    long countByWorkspaceId(UUID workspaceId);

    void deleteAllByWorkspaceId(UUID workspaceId);
}
