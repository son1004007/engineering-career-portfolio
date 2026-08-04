package io.github.son1004007.opsmate.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * 공개 방문자의 합성 구매 데이터 보존 범위를 나타내는 workspace.
 *
 * <p>브라우저 세션과 업무 row는 이 UUID로 결합된다. 만료 또는 종료 시 관련
 * 요청·발주·감사 이벤트를 함께 삭제해 다른 방문자에게 남지 않게 한다.
 */
@Entity
@Table(name = "demo_workspaces")
public class DemoWorkspace {

    @Id
    private UUID id;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DemoWorkspaceState state;

    protected DemoWorkspace() {
    }

    public static DemoWorkspace active(Instant now, Duration ttl) {
        DemoWorkspace workspace = new DemoWorkspace();
        workspace.id = UUID.randomUUID();
        workspace.createdAt = now;
        workspace.expiresAt = now.plus(ttl);
        workspace.state = DemoWorkspaceState.ACTIVE;
        return workspace;
    }

    public boolean isActiveAt(Instant now) {
        return state == DemoWorkspaceState.ACTIVE && expiresAt.isAfter(now);
    }

    public void close() {
        state = DemoWorkspaceState.CLOSED;
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public DemoWorkspaceState getState() {
        return state;
    }
}
