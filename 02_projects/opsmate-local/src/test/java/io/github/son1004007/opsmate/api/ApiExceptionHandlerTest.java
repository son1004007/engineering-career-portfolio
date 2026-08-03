package io.github.son1004007.opsmate.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import io.github.son1004007.opsmate.domain.PurchaseRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void optimisticLockFailureIsNormalizedToConflict() {
        var problem = handler.handleOptimisticConflict(
                new ObjectOptimisticLockingFailureException(PurchaseRequest.class, UUID.randomUUID()));

        assertThat(problem.getStatus()).isEqualTo(409);
        assertThat(problem.getProperties()).containsEntry("code", "WRITE_CONFLICT");
    }

    @Test
    void databaseConstraintFailureIsNormalizedToConflict() {
        var problem = handler.handleConstraint(new DataIntegrityViolationException("synthetic constraint failure"));

        assertThat(problem.getStatus()).isEqualTo(409);
        assertThat(problem.getProperties()).containsEntry("code", "WRITE_CONFLICT");
    }
}
