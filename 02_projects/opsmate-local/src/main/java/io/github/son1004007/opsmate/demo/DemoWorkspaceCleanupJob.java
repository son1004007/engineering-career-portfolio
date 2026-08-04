package io.github.son1004007.opsmate.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 만료된 공개 데모 workspace와 합성 업무 데이터를 주기적으로 제거한다. */
@Component
@ConditionalOnProperty(name = "opsmate.demo.enabled", havingValue = "true")
public class DemoWorkspaceCleanupJob {

    private final DemoWorkspaceService service;

    public DemoWorkspaceCleanupJob(DemoWorkspaceService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${opsmate.demo.cleanup-interval:10m}")
    public void deleteExpiredWorkspaces() {
        service.cleanupExpired();
    }
}
