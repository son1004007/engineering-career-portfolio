package io.github.son1004007.opsmate.application;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ActorProvider {

    public String currentActor() {
        return currentAuthentication().getName();
    }

    public Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "Authentication is required");
        }
        return authentication;
    }
}
