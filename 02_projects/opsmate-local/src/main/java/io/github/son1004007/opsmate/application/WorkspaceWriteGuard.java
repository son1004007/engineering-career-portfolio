package io.github.son1004007.opsmate.application;

import java.time.Instant;
import java.util.UUID;

import io.github.son1004007.opsmate.demo.DemoWorkspace;
import io.github.son1004007.opsmate.demo.DemoWorkspaceRepository;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.stereotype.Component;

/** DB 쓰기 직전에 공개 workspace의 ACTIVE/TTL 불변식을 잠금과 함께 재확인한다. */
@Component
public class WorkspaceWriteGuard {

    private final DemoWorkspaceRepository repository;

    public WorkspaceWriteGuard(DemoWorkspaceRepository repository) {
        this.repository = repository;
    }

    /**
     * 모델 같은 외부 I/O 뒤에도 workspace가 계속 유효한지 확인한다.
     *
     * <p>행 잠금은 이 메서드를 감싼 쓰기 transaction이 끝날 때까지 cleanup/delete와
     * 순서를 직렬화한다. 로컬 Basic API의 합성 고정 workspace는 공개 TTL 대상이 아니다.
     */
    public void requireActiveForWrite(UUID workspaceId, Instant now) {
        if (ActorProvider.LOCAL_WORKSPACE_ID.equals(workspaceId)) {
            return;
        }
        DemoWorkspace workspace = repository.findByIdForUpdate(workspaceId)
                .orElseThrow(() -> new OpsMateException(
                        ErrorCode.SESSION_EXPIRED,
                        "The demo workspace has expired"));
        if (!workspace.isActiveAt(now)) {
            throw new OpsMateException(ErrorCode.SESSION_EXPIRED, "The demo workspace has expired");
        }
    }
}
