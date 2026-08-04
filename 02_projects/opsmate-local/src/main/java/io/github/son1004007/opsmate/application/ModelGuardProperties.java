package io.github.son1004007.opsmate.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 단일 공개 앱 인스턴스가 제한된 GPU에 허용할 queue·동시 실행·workspace·전체 호출량 경계. */
@ConfigurationProperties(prefix = "opsmate.model-guard")
public class ModelGuardProperties {

    private int maxConcurrent = 1;
    private Duration queueWait = Duration.ofSeconds(2);
    private Duration workspaceWindow = Duration.ofHours(1);
    private int maxRequestsPerWorkspace = 10;
    private int maxRequestsGlobal = 100;
    private int maxFollowersPerFlight = 5;
    private Duration flightWait = Duration.ofSeconds(35);

    /** 잘못된 0·음수 제한값이 rate limit이나 queue 보호를 무력화하지 않게 시작 단계에서 거부한다. */
    public void validate() {
        if (maxConcurrent < 1
                || maxRequestsPerWorkspace < 1
                || maxRequestsGlobal < 1
                || maxFollowersPerFlight < 1
                || queueWait == null
                || queueWait.isZero()
                || queueWait.isNegative()
                || workspaceWindow == null
                || workspaceWindow.isZero()
                || workspaceWindow.isNegative()
                || flightWait == null
                || flightWait.isZero()
                || flightWait.isNegative()) {
            throw new IllegalStateException("Model guard limits and durations must be positive");
        }
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public Duration getQueueWait() {
        return queueWait;
    }

    public void setQueueWait(Duration queueWait) {
        this.queueWait = queueWait;
    }

    public Duration getWorkspaceWindow() {
        return workspaceWindow;
    }

    public void setWorkspaceWindow(Duration workspaceWindow) {
        this.workspaceWindow = workspaceWindow;
    }

    public int getMaxRequestsPerWorkspace() {
        return maxRequestsPerWorkspace;
    }

    public void setMaxRequestsPerWorkspace(int maxRequestsPerWorkspace) {
        this.maxRequestsPerWorkspace = maxRequestsPerWorkspace;
    }

    public int getMaxRequestsGlobal() {
        return maxRequestsGlobal;
    }

    public void setMaxRequestsGlobal(int maxRequestsGlobal) {
        this.maxRequestsGlobal = maxRequestsGlobal;
    }

    public int getMaxFollowersPerFlight() {
        return maxFollowersPerFlight;
    }

    public void setMaxFollowersPerFlight(int maxFollowersPerFlight) {
        this.maxFollowersPerFlight = maxFollowersPerFlight;
    }

    public Duration getFlightWait() {
        return flightWait;
    }

    public void setFlightWait(Duration flightWait) {
        this.flightWait = flightWait;
    }
}
