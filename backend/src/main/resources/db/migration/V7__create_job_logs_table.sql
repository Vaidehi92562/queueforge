CREATE TABLE job_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    job_id UUID NOT NULL,
    execution_id UUID,

    log_level VARCHAR(20) NOT NULL,
    message VARCHAR(3000) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_job_logs_job
        FOREIGN KEY (job_id)
        REFERENCES jobs(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_job_logs_execution
        FOREIGN KEY (execution_id)
        REFERENCES job_executions(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_job_logs_job_id
ON job_logs(job_id);

CREATE INDEX idx_job_logs_execution_id
ON job_logs(execution_id);

CREATE INDEX idx_job_logs_level
ON job_logs(log_level);

CREATE INDEX idx_job_logs_job_created
ON job_logs(job_id, created_at);
