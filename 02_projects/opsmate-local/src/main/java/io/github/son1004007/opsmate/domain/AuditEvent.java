package io.github.son1004007.opsmate.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * 한 workspace 안의 성공한 업무 상태 변경을 기록하는 애플리케이션 감사 이벤트.
 *
 * <p>자연어 원문, 전체 prompt와 credential은 기록하지 않는다. 업무 row와 같은
 * 트랜잭션에 저장해 상태만 바뀌고 성공 이벤트가 빠지는 상황을 방지한다.
 */
@Entity
@Table(
        name = "audit_events",
        indexes = @Index(
                name = "idx_audit_event_workspace_occurred",
                columnList = "workspace_id,occurred_at"))
public class AuditEvent {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "aggregate_type", nullable = false, length = 80)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 100)
    private String actor;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 500)
    private String metadata;

    protected AuditEvent() {
    }

    public static AuditEvent of(
            UUID workspaceId,
            Instant occurredAt,
            String aggregateType,
            UUID aggregateId,
            String actor,
            String action,
            String metadata) {
        AuditEvent event = new AuditEvent();
        event.id = UUID.randomUUID();
        event.workspaceId = workspaceId;
        event.occurredAt = occurredAt;
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.actor = actor;
        event.action = action;
        event.metadata = metadata;
        return event;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getMetadata() {
        return metadata;
    }
}
