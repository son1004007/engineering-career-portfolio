CREATE TABLE demo_workspaces (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    state VARCHAR(20) NOT NULL
);

CREATE TABLE purchase_requests (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    workspace_id UUID NOT NULL REFERENCES demo_workspaces(id) ON DELETE CASCADE,
    request_text VARCHAR(4000) NOT NULL,
    request_fingerprint VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    purpose VARCHAR(1000) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    category VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    requested_by VARCHAR(100) NOT NULL,
    decided_by VARCHAR(100),
    rejection_reason VARCHAR(500),
    idempotency_key VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_purchase_request_actor_idempotency
        UNIQUE (workspace_id, requested_by, idempotency_key)
);

CREATE INDEX idx_purchase_request_workspace_status_updated
    ON purchase_requests (workspace_id, status, updated_at);

CREATE TABLE purchase_request_policy_evidence (
    purchase_request_id UUID NOT NULL REFERENCES purchase_requests(id) ON DELETE CASCADE,
    policy_id VARCHAR(80) NOT NULL,
    PRIMARY KEY (purchase_request_id, policy_id)
);

CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES demo_workspaces(id) ON DELETE CASCADE,
    order_number VARCHAR(40) NOT NULL UNIQUE,
    purchase_request_id UUID NOT NULL REFERENCES purchase_requests(id) ON DELETE CASCADE,
    created_by VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    request_fingerprint VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_purchase_order_request UNIQUE (workspace_id, purchase_request_id),
    CONSTRAINT uk_purchase_order_actor_idempotency
        UNIQUE (workspace_id, created_by, idempotency_key)
);

CREATE INDEX idx_purchase_order_workspace_created
    ON purchase_orders (workspace_id, created_at);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES demo_workspaces(id) ON DELETE CASCADE,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    aggregate_type VARCHAR(80) NOT NULL,
    aggregate_id UUID NOT NULL,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(80) NOT NULL,
    metadata VARCHAR(500) NOT NULL
);

CREATE INDEX idx_audit_event_workspace_occurred
    ON audit_events (workspace_id, occurred_at);
