DROP TABLE IF EXISTS activity_snapshot;

CREATE TABLE activity_snapshot (
    id BIGINT PRIMARY KEY,
    tenant_id VARCHAR(40) NOT NULL,
    snapshot_year CHAR(4) NOT NULL,
    snapshot_month CHAR(2) NOT NULL,
    item_key VARCHAR(60) NOT NULL,
    status VARCHAR(20) NOT NULL,
    score INTEGER NOT NULL
);

CREATE INDEX idx_activity_range
    ON activity_snapshot (tenant_id, snapshot_year, snapshot_month);
