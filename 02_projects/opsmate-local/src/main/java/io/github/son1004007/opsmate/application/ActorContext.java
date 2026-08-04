package io.github.son1004007.opsmate.application;

import java.util.UUID;

import org.springframework.security.core.Authentication;

/**
 * 인증 주체와 서버가 결정한 workspace를 함께 전달하는 불변 실행 문맥.
 *
 * <p>업무 서비스는 요청 body나 query parameter에서 workspace를 받지 않는다.
 * 공개 데모에서는 세션의 {@code DemoPrincipal}, 로컬 API에서는 고정된 검증
 * workspace를 사용해 모든 저장과 조회를 동일한 격리 기준으로 실행한다.
 */
public record ActorContext(UUID workspaceId, String actor, Authentication authentication) {

    public boolean hasRole(String role) {
        String authorityName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(authorityName));
    }
}
