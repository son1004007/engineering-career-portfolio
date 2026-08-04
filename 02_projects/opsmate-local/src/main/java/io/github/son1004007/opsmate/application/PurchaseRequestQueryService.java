package io.github.son1004007.opsmate.application;

import java.util.List;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 현재 workspace와 persona에 맞는 구매 요청 작업함을 최대 100건 반환한다.
 *
 * <p>목록 전체를 읽은 뒤 메모리에서 거르지 않고 repository query 자체에
 * workspace와 상태를 넣어 방문자 간 데이터가 섞일 가능성을 줄인다.
 */
@Service
public class PurchaseRequestQueryService {

    private final PurchaseRequestRepository repository;
    private final ActorProvider actorProvider;

    public PurchaseRequestQueryService(PurchaseRequestRepository repository, ActorProvider actorProvider) {
        this.repository = repository;
        this.actorProvider = actorProvider;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('REQUESTER', 'APPROVER', 'BUYER', 'AUDITOR')")
    public List<PurchaseRequest> findVisible() {
        ActorContext context = actorProvider.currentContext();
        if (context.hasRole("REQUESTER")) {
            return repository.findTop100ByWorkspaceIdAndRequestedByOrderByUpdatedAtDesc(
                    context.workspaceId(), context.actor());
        }
        if (context.hasRole("APPROVER")) {
            return repository.findTop100ByWorkspaceIdAndStatusOrderByUpdatedAtDesc(
                    context.workspaceId(), PurchaseRequestStatus.PENDING_APPROVAL);
        }
        if (context.hasRole("BUYER")) {
            return repository.findTop100ByWorkspaceIdAndStatusInOrderByUpdatedAtDesc(
                    context.workspaceId(),
                    List.of(PurchaseRequestStatus.APPROVED, PurchaseRequestStatus.ORDERED));
        }
        if (context.hasRole("AUDITOR")) {
            return repository.findTop100ByWorkspaceIdOrderByUpdatedAtDesc(context.workspaceId());
        }
        throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "No readable purchase-request work queue");
    }
}
