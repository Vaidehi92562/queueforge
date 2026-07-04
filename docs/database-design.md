# QueueForge Database Design

## Overview

QueueForge uses PostgreSQL as the primary relational database.

The database is designed to support authentication, organizations, projects, queues, jobs, workers, retries, logs, metrics, scheduled jobs, and dead letter queue entries.

The schema follows normalization principles and uses indexes for high-frequency operations such as job claiming, job filtering, worker heartbeat checks, and queue metrics.

## Core Tables

### 1. users

Stores registered users.

Fields:

- id
- name
- email
- password_hash
- role
- created_at
- updated_at

Important constraints:

- email must be unique
- role should be ADMIN, DEVELOPER, or VIEWER

### 2. organizations

Stores teams or organizations.

Fields:

- id
- name
- owner_id
- created_at
- updated_at

Relationships:

- one organization belongs to one owner user
- one organization can have many projects

### 3. organization_members

Stores user membership inside an organization.

Fields:

- id
- organization_id
- user_id
- role
- joined_at

Relationships:

- many users can belong to one organization
- one user can belong to many organizations

### 4. projects

Stores projects under organizations.

Fields:

- id
- organization_id
- name
- description
- created_at
- updated_at

Relationships:

- one organization has many projects
- one project has many queues

### 5. queues

Stores job queue configuration.

Fields:

- id
- project_id
- name
- priority
- max_concurrency
- is_paused
- rate_limit_per_minute
- retry_policy_id
- created_at
- updated_at

Relationships:

- one project has many queues
- one queue has many jobs
- one queue can use one retry policy

### 6. retry_policies

Stores retry configuration.

Fields:

- id
- name
- strategy
- max_attempts
- base_delay_seconds
- max_delay_seconds
- created_at
- updated_at

Supported strategies:

- FIXED_DELAY
- LINEAR_BACKOFF
- EXPONENTIAL_BACKOFF

### 7. jobs

Main table for all executable jobs.

Fields:

- id
- queue_id
- project_id
- type
- status
- payload
- priority
- run_at
- cron_expression
- max_attempts
- current_attempt
- idempotency_key
- locked_by_worker_id
- locked_at
- created_at
- updated_at

Supported job types:

- IMMEDIATE
- DELAYED
- SCHEDULED
- RECURRING
- BATCH

Supported job statuses:

- QUEUED
- SCHEDULED
- CLAIMED
- RUNNING
- COMPLETED
- FAILED
- RETRYING
- DEAD_LETTER
- CANCELLED

### 8. job_executions

Stores every execution attempt of a job.

Fields:

- id
- job_id
- worker_id
- attempt_number
- status
- started_at
- completed_at
- duration_ms
- error_message
- created_at

Relationships:

- one job can have many execution attempts
- one worker can execute many job attempts

### 9. job_logs

Stores logs related to jobs and executions.

Fields:

- id
- job_id
- execution_id
- log_level
- message
- created_at

Log levels:

- INFO
- WARN
- ERROR

### 10. workers

Stores registered worker instances.

Fields:

- id
- worker_name
- status
- max_concurrency
- current_running_jobs
- last_heartbeat_at
- started_at
- updated_at

Worker statuses:

- ONLINE
- OFFLINE
- DRAINING
- SHUTTING_DOWN

### 11. worker_heartbeats

Stores heartbeat history for workers.

Fields:

- id
- worker_id
- heartbeat_at
- running_jobs
- metadata

### 12. scheduled_jobs

Stores recurring or scheduled job definitions.

Fields:

- id
- queue_id
- project_id
- name
- cron_expression
- payload_template
- is_active
- next_run_at
- last_run_at
- created_at
- updated_at

### 13. dead_letter_queue

Stores permanently failed jobs.

Fields:

- id
- job_id
- queue_id
- project_id
- final_error_message
- failed_attempts
- moved_at
- requeued_at
- status

### 14. idempotency_keys

Prevents duplicate job creation.

Fields:

- id
- idempotency_key
- request_hash
- job_id
- created_at
- expires_at

## Important Indexes

### Job claiming index

Used by workers to quickly find claimable jobs.

Index:

jobs(status, queue_id, run_at, priority, created_at)

### Job filtering index

Used by dashboard job explorer.

Index:

jobs(project_id, status, created_at)

### Worker heartbeat index

Used to detect offline workers.

Index:

workers(last_heartbeat_at)

### Execution history index

Used to fetch job execution attempts.

Index:

job_executions(job_id, attempt_number)

### Dead Letter Queue index

Used to search failed jobs.

Index:

dead_letter_queue(project_id, moved_at)

### Idempotency index

Used to prevent duplicate job creation.

Index:

idempotency_keys(idempotency_key)

## Cascading Behavior

- If an organization is deleted, its projects should be deleted.
- If a project is deleted, its queues and jobs should be deleted.
- If a queue is deleted, its jobs should be deleted.
- Job execution history and logs should be linked to jobs.
- Dead letter queue entries should preserve failure information for debugging.

## Performance Considerations

The jobs table will be the hottest table in the system.

To keep job claiming fast:

- Use indexes on status, queue_id, run_at, priority, and created_at.
- Use pagination for job explorer APIs.
- Store job payload as JSONB.
- Avoid full table scans on jobs.
- Use PostgreSQL FOR UPDATE SKIP LOCKED for atomic concurrent job claiming.
- Keep execution logs separate from the jobs table.
- Keep heartbeat history separate from the workers table.

## Normalization

The schema separates users, organizations, projects, queues, jobs, executions, logs, workers, retry policies, and dead letter entries.

This avoids duplicate data and keeps each table focused on one responsibility.

## Database Design Goal

The database is designed to support reliability, concurrency, observability, and maintainability instead of just storing simple CRUD data.
