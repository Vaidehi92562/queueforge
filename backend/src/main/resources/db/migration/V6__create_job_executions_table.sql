CREATE TABLE job_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    job_id UUID NOT NULL,
    worker_id UUID,

    attempt_number INTEGER NOT NULL,
    status VARCHAR(40) NOT NULL,

    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    duration_ms BIGINT,

    error_message VARCHAR(2000),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_job_executions_job
        FOREIGN KEY (job_id)
        REFERENCES jobs(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_job_executions_attempt
        CHECK (attempt_number >= 1),

    CONSTRAINT chk_job_executions_duration
        CHECK (duration_ms IS NULL OR duration_ms >= 0)
);

CREATE INDEX idx_job_executions_job_id
ON job_executions(job_id);

CREATE INDEX idx_job_executions_job_attempt
ON job_executions(job_id, attempt_number);

CREATE INDEX idx_job_executions_worker_id
ON job_executions(worker_id);

CREATE INDEX idx_job_executions_status
ON job_executions(status);
