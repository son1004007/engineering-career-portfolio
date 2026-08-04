package io.github.son1004007.opsmate.application;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequestReadPolicy {

    /**
     * workspace 격리를 역할 정책보다 먼저 적용한 뒤 객체 단위 읽기 권한을 확인한다.
     *
     * <p>비감사 역할은 존재하지 않는 UUID와 읽을 수 없는 UUID를 모두 같은 거부로
     * 처리해 객체 존재 여부를 이용한 열거를 어렵게 한다.
     */
    public void requireReadable(PurchaseRequest request, ActorContext context) {
        if (!request.getWorkspaceId().equals(context.workspaceId())
                || !canRead(request, context.authentication())) {
            throw forbidden();
        }
    }

    public boolean canDistinguishNotFound(Authentication authentication) {
        return hasRole(authentication, "ROLE_AUDITOR");
    }

    private boolean canRead(PurchaseRequest request, Authentication authentication) {
        if (hasRole(authentication, "ROLE_AUDITOR")) {
            return true;
        }
        if (hasRole(authentication, "ROLE_REQUESTER")
                && request.getRequestedBy().equals(authentication.getName())) {
            return true;
        }
        if (hasRole(authentication, "ROLE_APPROVER")
                && request.getStatus() == PurchaseRequestStatus.PENDING_APPROVAL) {
            return true;
        }
        return hasRole(authentication, "ROLE_BUYER")
                && (request.getStatus() == PurchaseRequestStatus.APPROVED
                || request.getStatus() == PurchaseRequestStatus.ORDERED);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private OpsMateException forbidden() {
        return new OpsMateException(
                ErrorCode.UNAUTHORIZED_ACTION,
                "The purchase request is not readable in this role and state");
    }
}
