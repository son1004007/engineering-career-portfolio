package io.github.son1004007.opsmate.demo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

/**
 * 서버가 HttpSession에 저장하는 공개 데모 전용 인증 주체.
 *
 * <p>workspace와 persona는 서버가 생성·전환한다. actor 값도 이 두 값에서
 * 결정되므로 브라우저가 다른 방문자의 workspace나 임의 계정을 주입할 수 없다.
 */
public record DemoPrincipal(UUID workspaceId, DemoPersona persona) implements Serializable {

    public String actor() {
        // workspace는 별도 DB 조건으로 격리하므로 actor 표시에 UUID 일부를 섞지 않는다.
        return persona.name().toLowerCase();
    }

    public List<GrantedAuthority> authorities() {
        return List.of(persona.authority());
    }
}
