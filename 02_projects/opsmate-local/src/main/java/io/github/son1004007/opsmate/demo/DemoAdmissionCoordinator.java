package io.github.son1004007.opsmate.demo;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * 단일 공개 앱 인스턴스에서 workspace 수용량의 count→insert를 직렬화한다.
 *
 * <p>transaction service 호출을 monitor 안에서 끝내므로 DB commit까지 완료된 뒤 다음
 * 입장을 검사한다. 여러 앱 인스턴스로 확장할 때는 이 JVM lock을 DB advisory lock이나
 * 분산 permit으로 교체해야 한다.
 */
@Component
public class DemoAdmissionCoordinator {

    private final DemoWorkspaceService workspaceService;
    private final DemoAdmissionLimiter admissionLimiter;

    public DemoAdmissionCoordinator(
            DemoWorkspaceService workspaceService,
            DemoAdmissionLimiter admissionLimiter) {
        this.workspaceService = workspaceService;
        this.admissionLimiter = admissionLimiter;
    }

    public synchronized DemoWorkspace start() {
        admissionLimiter.acquire();
        return workspaceService.start();
    }

    public synchronized DemoWorkspace reset(UUID workspaceId) {
        // quota를 먼저 확인해 제한된 reset이 기존 workspace를 삭제하는 부분 실패를 막는다.
        admissionLimiter.acquire();
        return workspaceService.reset(workspaceId);
    }
}
