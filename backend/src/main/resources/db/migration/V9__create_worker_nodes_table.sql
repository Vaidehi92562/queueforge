CREATE TABLE worker_nodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(160) NOT NULL UNIQUE,
    status VARCHAR(40) NOT NULL,

    max_concurrency INTEGER NOT NULL DEFAULT 5,
    current_load INTEGER NOT NULL DEFAULT 0,

    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_heartbeat_at TIMESTAMPTZ,

    shutdown_requested BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_worker_max_concurrency
        CHECK (max_concurrency >= 1 AND max_concurrency <= 100),

    CONSTRAINT chk_worker_current_load
        CHECK (current_load >= 0)
);

CREATE INDEX idx_worker_nodes_status
ON worker_nodes(status);

CREATE INDEX idx_worker_nodes_last_heartbeat
ON worker_nodes(last_heartbeat_at);

CREATE INDEX idx_worker_nodes_shutdown_requested
ON worker_nodes(shutdown_requested);
