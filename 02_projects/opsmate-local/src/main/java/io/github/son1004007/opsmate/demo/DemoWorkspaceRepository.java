package io.github.son1004007.opsmate.demo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoWorkspaceRepository extends JpaRepository<DemoWorkspace, UUID> {

    long countByStateAndExpiresAtAfter(DemoWorkspaceState state, Instant now);

    boolean existsByIdAndStateAndExpiresAtAfter(
            UUID id,
            DemoWorkspaceState state,
            Instant now);

    List<DemoWorkspace> findAllByExpiresAtLessThanEqual(Instant now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select workspace from DemoWorkspace workspace where workspace.id = :id")
    java.util.Optional<DemoWorkspace> findByIdForUpdate(@Param("id") UUID id);
}
