package io.github.son1004007.opsmate.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    private UUID id;

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
            Instant occurredAt,
            String aggregateType,
            UUID aggregateId,
            String actor,
            String action,
            String metadata) {
        AuditEvent event = new AuditEvent();
        event.id = UUID.randomUUID();
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
