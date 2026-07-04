CREATE TABLE queues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    retry_policy_id UUID,
    name VARCHAR(140) NOT NULL,
    priority INTEGER NOT NULL DEFAULT 5,
    max_concurrency INTEGER NOT NULL DEFAULT 5,
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    rate_limit_per_minute INTEGER NOT NULL DEFAULT 100,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_queues_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_queues_retry_policy
        FOREIGN KEY (retry_policy_id)
        REFERENCES retry_policies(id)
        ON DELETE SET NULL,

    CONSTRAINT uq_queue_name_per_project
        UNIQUE (project_id, name),

    CONSTRAINT chk_queue_priority
        CHECK (priority >= 0 AND priority <= 100),

    CONSTRAINT chk_queue_max_concurrency
        CHECK (max_concurrency >= 1 AND max_concurrency <= 100),

    CONSTRAINT chk_queue_rate_limit
        CHECK (rate_limit_per_minute >= 1)
);

CREATE INDEX idx_queues_project_id ON queues(project_id);
CREATE INDEX idx_queues_retry_policy_id ON queues(retry_policy_id);
CREATE INDEX idx_queues_paused ON queues(is_paused);
CREATE INDEX idx_queues_project_priority ON queues(project_id, priority);
