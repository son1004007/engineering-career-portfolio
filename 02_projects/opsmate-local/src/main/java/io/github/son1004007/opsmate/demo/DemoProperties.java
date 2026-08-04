package io.github.son1004007.opsmate.demo;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 공개 workspace의 생성 gate, 보존 시간, 정리 주기와 단일 인스턴스 수용량 설정. */
@ConfigurationProperties(prefix = "opsmate.demo")
public class DemoProperties {

    private boolean enabled;
    private boolean startEnabled;
    private Duration workspaceTtl = Duration.ofMinutes(30);
    private Duration cleanupInterval = Duration.ofMinutes(10);
    private int maxActiveWorkspaces = 100;
    private Duration admissionWindow = Duration.ofHours(1);
    private int maxStartsGlobal = 200;

    /** 만료·정리·수용량 통제가 0 또는 음수 설정으로 무력화되는 것을 시작 단계에서 거부한다. */
    public void validate() {
        if (workspaceTtl == null
                || workspaceTtl.isZero()
                || workspaceTtl.isNegative()
                || cleanupInterval == null
                || cleanupInterval.isZero()
                || cleanupInterval.isNegative()
                || maxActiveWorkspaces < 1
                || admissionWindow == null
                || admissionWindow.isZero()
                || admissionWindow.isNegative()
                || maxStartsGlobal < 1) {
            throw new IllegalStateException("Demo TTL, cleanup interval and capacity must be positive");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStartEnabled() {
        return startEnabled;
    }

    public void setStartEnabled(boolean startEnabled) {
        this.startEnabled = startEnabled;
    }

    public Duration getWorkspaceTtl() {
        return workspaceTtl;
    }

    public void setWorkspaceTtl(Duration workspaceTtl) {
        this.workspaceTtl = workspaceTtl;
    }

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public int getMaxActiveWorkspaces() {
        return maxActiveWorkspaces;
    }

    public void setMaxActiveWorkspaces(int maxActiveWorkspaces) {
        this.maxActiveWorkspaces = maxActiveWorkspaces;
    }

    public Duration getAdmissionWindow() {
        return admissionWindow;
    }

    public void setAdmissionWindow(Duration admissionWindow) {
        this.admissionWindow = admissionWindow;
    }

    public int getMaxStartsGlobal() {
        return maxStartsGlobal;
    }

    public void setMaxStartsGlobal(int maxStartsGlobal) {
        this.maxStartsGlobal = maxStartsGlobal;
    }
}
