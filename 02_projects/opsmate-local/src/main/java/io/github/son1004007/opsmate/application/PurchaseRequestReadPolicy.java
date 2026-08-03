package io.github.son1004007.opsmate.application;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequestReadPolicy {

    public void requireReadable(PurchaseRequest request, Authentication authentication) {
        if (!canRead(request, authentication)) {
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
