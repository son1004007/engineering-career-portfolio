package io.github.son1004007.opsmate.demo;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 한 방문자가 합성 업무 흐름을 순서대로 체험하기 위해 선택하는 데모 역할.
 *
 * <p>실제 조직의 신원이나 직무 분리를 증명하지 않으며, 한 요청에는 정확히
 * 한 persona의 authority만 부여한다.
 */
public enum DemoPersona {
    REQUESTER,
    APPROVER,
    BUYER,
    AUDITOR;

    public SimpleGrantedAuthority authority() {
        return new SimpleGrantedAuthority("ROLE_" + name());
    }
}
