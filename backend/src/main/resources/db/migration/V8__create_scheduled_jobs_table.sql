CREATE TABLE scheduled_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    queue_id UUID NOT NULL,
    project_id UUID NOT NULL,

    name VARCHAR(140) NOT NULL,
    cron_expression VARCHAR(120) NOT NULL,
    payload_template JSONB NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    next_run_at TIMESTAMPTZ,
    last_run_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_scheduled_jobs_queue
        FOREIGN KEY (queue_id)
        REFERENCES queues(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_scheduled_jobs_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_scheduled_jobs_project_id
ON scheduled_jobs(project_id);

CREATE INDEX idx_scheduled_jobs_queue_id
ON scheduled_jobs(queue_id);

CREATE INDEX idx_scheduled_jobs_active_next_run
ON scheduled_jobs(is_active, next_run_at);

CREATE INDEX idx_scheduled_jobs_payload_gin
ON scheduled_jobs USING GIN(payload_template);
