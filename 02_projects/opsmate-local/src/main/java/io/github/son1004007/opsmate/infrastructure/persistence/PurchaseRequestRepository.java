package io.github.son1004007.opsmate.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import io.github.son1004007.opsmate.domain.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {

    Optional<PurchaseRequest> findByRequestedByAndIdempotencyKey(String requestedBy, String idempotencyKey);
}
