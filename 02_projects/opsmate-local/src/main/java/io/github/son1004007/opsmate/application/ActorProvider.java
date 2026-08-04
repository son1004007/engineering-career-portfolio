package io.github.son1004007.opsmate.application;

import java.time.Clock;
import java.util.UUID;

import io.github.son1004007.opsmate.demo.DemoPrincipal;
import io.github.son1004007.opsmate.demo.DemoWorkspaceRepository;
import io.github.son1004007.opsmate.demo.DemoWorkspaceState;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Security 인증에서 서비스가 신뢰할 actor와 workspace 범위를 한 번에 구성한다.
 *
 * <p>브라우저가 보낸 form 값은 사용하지 않는다. 공개 demo principal은 DB의 ACTIVE/TTL을
 * 매번 확인하고, 로컬 Basic API만 합성 고정 workspace를 사용한다.
 */
@Component
public class ActorProvider {

    static final UUID LOCAL_WORKSPACE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final DemoWorkspaceRepository workspaceRepository;
    private final Clock clock;

    public ActorProvider(DemoWorkspaceRepository workspaceRepository, Clock clock) {
        this.workspaceRepository = workspaceRepository;
        this.clock = clock;
    }

    public String currentActor() {
        return currentContext().actor();
    }

    public UUID currentWorkspaceId() {
        return currentContext().workspaceId();
    }

    /**
     * 현재 인증에서 신뢰 가능한 actor와 workspace를 구성한다.
     *
     * <p>DemoPrincipal의 workspace는 매 요청마다 ACTIVE/TTL을 다시 확인한다.
     * 만료된 HttpSession이 남아 있어도 업무 데이터를 읽거나 쓰지 못하게 하기 위한
     * 방어선이다. 로컬 Basic API는 자동화 검증용 고정 workspace를 사용한다.
     */
    public ActorContext currentContext() {
        Authentication authentication = currentAuthentication();
        if (authentication.getPrincipal() instanceof DemoPrincipal principal) {
            boolean active = workspaceRepository.existsByIdAndStateAndExpiresAtAfter(
                    principal.workspaceId(),
                    DemoWorkspaceState.ACTIVE,
                    clock.instant());
            if (!active) {
                throw new OpsMateException(ErrorCode.SESSION_EXPIRED, "The demo workspace has expired");
            }
            return new ActorContext(principal.workspaceId(), principal.actor(), authentication);
        }
        return new ActorContext(LOCAL_WORKSPACE_ID, authentication.getName(), authentication);
    }

    public Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "Authentication is required");
        }
        return authentication;
    }
}
