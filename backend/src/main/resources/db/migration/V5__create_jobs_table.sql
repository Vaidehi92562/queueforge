CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    queue_id UUID NOT NULL,
    project_id UUID NOT NULL,

    type VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,

    payload JSONB NOT NULL,

    priority INTEGER NOT NULL DEFAULT 5,
    run_at TIMESTAMPTZ,

    cron_expression VARCHAR(120),

    max_attempts INTEGER NOT NULL DEFAULT 3,
    current_attempt INTEGER NOT NULL DEFAULT 0,

    idempotency_key VARCHAR(160),

    locked_by_worker_id UUID,
    locked_at TIMESTAMPTZ,

    claimed_at TIMESTAMPTZ,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    last_error_message VARCHAR(2000),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_jobs_queue
        FOREIGN KEY (queue_id)
        REFERENCES queues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_jobs_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_jobs_priority
        CHECK (priority >= 0 AND priority <= 100),

    CONSTRAINT chk_jobs_max_attempts
        CHECK (max_attempts >= 1),

    CONSTRAINT chk_jobs_current_attempt
        CHECK (current_attempt >= 0)
);

CREATE UNIQUE INDEX uq_jobs_idempotency_key
ON jobs(idempotency_key)
WHERE idempotency_key IS NOT NULL;

CREATE INDEX idx_jobs_project_id ON jobs(project_id);
CREATE INDEX idx_jobs_queue_id ON jobs(queue_id);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_type ON jobs(type);

CREATE INDEX idx_jobs_project_status_created
ON jobs(project_id, status, created_at);

CREATE INDEX idx_jobs_queue_status_run_priority
ON jobs(queue_id, status, run_at, priority, created_at);

CREATE INDEX idx_jobs_claim_ready
ON jobs(status, run_at, priority, created_at)
WHERE status = 'QUEUED';

CREATE INDEX idx_jobs_payload_gin
ON jobs USING GIN(payload);
