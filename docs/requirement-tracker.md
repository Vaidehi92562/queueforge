# QueueForge Requirement Tracker

This document tracks assignment requirements, our implementation plan, current status, and marks impact.

## Overall Progress

| Area | Marks | Status |
|---|---:|---|
| System Architecture | 20 | In Progress |
| Database Design | 20 | In Progress |
| Backend Engineering | 20 | Not Started |
| Reliability & Concurrency | 15 | Planned |
| Frontend & UX | 10 | Not Started |
| API Design | 5 | Planned |
| Documentation | 5 | In Progress |
| Testing | 5 | Planned |

## Requirement Comparison Table

| Assignment Requirement | Our Implementation | Status |
|---|---|---|
| Authentication | JWT login/register with roles | Planned |
| Project management | User, organization, project model | Planned |
| Multiple queues per project | Project can have many queues | Planned |
| Queue priority | Queue priority and job priority | Planned |
| Queue concurrency limits | max_concurrency per queue | Planned |
| Retry policy | Fixed, linear, exponential retry policies | Planned |
| Pause/resume queue | Pause/resume APIs | Planned |
| Queue statistics | Queue metrics dashboard | Planned |
| Immediate jobs | Immediate job creation API | Planned |
| Delayed jobs | run_at based delayed jobs | Planned |
| Scheduled jobs | Scheduler service | Planned |
| Recurring cron jobs | cron_expression support | Planned |
| Batch jobs | Batch job creation API | Planned |
| Worker polling | Worker service polls queues | Planned |
| Atomic job claiming | PostgreSQL FOR UPDATE SKIP LOCKED | Planned |
| Concurrent execution | Worker thread pool | Planned |
| Worker heartbeat | Worker heartbeat table and API | Planned |
| Graceful shutdown | Worker draining mode | Planned |
| Job lifecycle | QUEUED to COMPLETED / FAILED / DLQ | Planned |
| Retries | Retry count and retry history | Planned |
| Dead Letter Queue | DLQ table and requeue API | Planned |
| Execution logs | job_logs and job_executions tables | Planned |
| Worker assignment | execution linked to worker | Planned |
| Timestamps | created, updated, run, claim, completion timestamps | Planned |
| Execution metrics | duration, attempts, success/failure | Planned |
| Web dashboard | React dashboard | Planned |
| Inspect jobs | Job explorer and job detail page | Planned |
| Monitor workers | Worker monitoring page | Planned |
| Retry failed jobs | Retry/requeue buttons | Planned |
| Throughput visualization | Metrics cards and charts | Planned |
| System health | Active workers, failed jobs, queue lag | Planned |
| API validation | DTO validation and global error handler | Planned |
| Pagination/filtering | Job list APIs with filters | Planned |
| Structured logging | Backend logs and job logs | Planned |
| Idempotency | Idempotency key for duplicate prevention | Planned |
| ER diagram | To be added in docs | Planned |
| Architecture diagram | Architecture blueprint added | In Progress |
| API documentation | API planning document added | In Progress |
| Design decisions document | Trade-offs document added | Done |
| Automated tests | Critical tests planned | Planned |

## Phase Tracker

| Phase | Name | Status |
|---|---|---|
| Phase 0 | Blueprint and Setup | In Progress |
| Phase 1 | Backend Foundation | Done |
| Phase 2 | Queue Management | Done |
| Phase 3 | Job Creation Engine | Not Started |
| Phase 4 | Worker Engine | Not Started |
| Phase 5 | Reliability Layer | Not Started |
| Phase 6 | Frontend Dashboard | Not Started |
| Phase 7 | Bonus Features | Not Started |
| Phase 8 | Testing | Not Started |
| Phase 9 | Documentation and Final Polish | Not Started |

## Phase 0 Completion Checklist

| Task | Status |
|---|---|
| Project folder created | Done |
| Backend folder created | Done |
| Frontend folder created | Done |
| Docs folder created | Done |
| Root README created | Done |
| Gitignore created | Done |
| Architecture blueprint created | Done |
| Database design blueprint created | Done |
| API documentation plan created | Done |
| Design decisions document created | Done |
| Testing strategy document created | Done |
| Docker compose placeholder created | Done |
| Backend README created | Done |
| Frontend README created | Done |
| Requirement tracker created | Done |


## Local Tool Check

| Tool | Status | Version / Note |
|---|---|---|
| Java | Done | OpenJDK 25 installed |
| Maven | Done | Apache Maven 3.9.14 installed |
| Node.js | Done | Node v25.9.0 installed |
| npm | Done | npm 11.12.1 installed |
| Docker | Pending | Docker command not found; install Docker Desktop later |

## Phase 0 Final Status

Phase 0 blueprint and setup is complete.

Next phase:

Phase 1 - Backend Foundation


## Phase 1 Completion Summary

| Requirement / Feature | Implementation | Status |
|---|---|---|
| Backend Spring Boot setup | Maven project with Spring Boot | Done |
| PostgreSQL connection | Docker PostgreSQL container | Done |
| Flyway migrations | V1 users, V2 organizations/projects | Done |
| Health API | GET /api/health | Done |
| Authentication | JWT register/login | Done |
| User model | users table + User entity | Done |
| Role support | ADMIN, DEVELOPER, VIEWER enum | Done |
| Organization management | Create/list organizations | Done |
| Organization membership | organization_members table + entity | Done |
| Project management | Create/list projects under organization | Done |
| Protected APIs | JWT Authorization header required | Done |
| API response format | Common ApiResponse wrapper | Done |
| Error handling | GlobalExceptionHandler | Done |
| Runtime API testing | curl-based register/login/org/project tests | Done |

## Phase 1 Final Status

Phase 1 - Backend Foundation is complete.

Next phase:

Phase 2 - Queue Management

Phase 2 will implement:

- Queue entity
- Retry policy entity
- Queue creation API
- Queue listing API
- Pause/resume queue APIs
- Queue priority
- Queue max concurrency
- Queue rate limit
- Queue statistics foundation


## Phase 2 Completion Summary

| Requirement / Feature | Implementation | Status |
|---|---|---|
| Retry policy entity | RetryPolicy entity with strategy, max attempts, base delay, max delay | Done |
| Retry strategies | FIXED_DELAY, LINEAR_BACKOFF, EXPONENTIAL_BACKOFF enum support | Done |
| Retry policy migration | V3__create_retry_policies_table.sql | Done |
| Retry policy APIs | POST /api/retry-policies, GET /api/retry-policies | Done |
| Queue entity | JobQueue entity mapped to queues table | Done |
| Queue configuration | priority, maxConcurrency, rateLimitPerMinute, retryPolicy | Done |
| Queue migration | V4__create_queues_table.sql | Done |
| Queue create API | POST /api/queues | Done |
| Queue list API | GET /api/queues?projectId=<id> | Done |
| Queue pause API | PATCH /api/queues/{queueId}/pause | Done |
| Queue resume API | PATCH /api/queues/{queueId}/resume | Done |
| Queue stats foundation | GET /api/queues/{queueId}/stats | Done |
| Queue access control | User must belong to project organization | Done |
| Runtime testing | Retry policy, queue, pause, resume, stats APIs tested with curl | Done |

## Phase 2 Final Status

Phase 2 - Queue Management is complete.

Next phase:

Phase 3 - Job Creation Engine

Phase 3 will implement:

- Jobs table
- Job executions table
- Job logs table
- Scheduled jobs table
- Immediate job API
- Delayed job API
- Scheduled job API
- Recurring job API
- Batch job API
- Job list and filter API
- Job detail API


## Phase 2 Completion Summary

| Requirement / Feature | Implementation | Status |
|---|---|---|
| Retry policy entity | RetryPolicy entity with strategy, max attempts, base delay, max delay | Done |
| Retry strategies | FIXED_DELAY, LINEAR_BACKOFF, EXPONENTIAL_BACKOFF enum support | Done |
| Retry policy migration | V3__create_retry_policies_table.sql | Done |
| Retry policy APIs | POST /api/retry-policies, GET /api/retry-policies | Done |
| Queue entity | JobQueue entity mapped to queues table | Done |
| Queue configuration | priority, maxConcurrency, rateLimitPerMinute, retryPolicy | Done |
| Queue migration | V4__create_queues_table.sql | Done |
| Queue create API | POST /api/queues | Done |
| Queue list API | GET /api/queues?projectId=<id> | Done |
| Queue pause API | PATCH /api/queues/{queueId}/pause | Done |
| Queue resume API | PATCH /api/queues/{queueId}/resume | Done |
| Queue stats foundation | GET /api/queues/{queueId}/stats | Done |
| Queue access control | User must belong to project organization | Done |
| Runtime testing | Retry policy, queue, pause, resume, stats APIs tested with curl | Done |

## Phase 2 Final Status

Phase 2 - Queue Management is complete.

Next phase:

Phase 3 - Job Creation Engine

Phase 3 will implement:

- Jobs table
- Job executions table
- Job logs table
- Scheduled jobs table
- Immediate job API
- Delayed job API
- Scheduled job API
- Recurring job API
- Batch job API
- Job list and filter API
- Job detail API


## Phase 3 Completion Update — Job Creation Engine

| Requirement | Implementation Status | Evidence |
|---|---:|---|
| Immediate job creation API | Done | POST /api/jobs/immediate |
| Delayed job creation API | Done | POST /api/jobs/delayed with future runAt |
| Scheduled job creation API | Done | POST /api/jobs/scheduled |
| Recurring cron job definition | Done | POST /api/jobs/recurring and scheduled_jobs table |
| Batch job creation | Done | POST /api/jobs/batch |
| Job lifecycle fields | Done | status, attempts, runAt, claimedAt, startedAt, completedAt |
| Idempotency support | Done | unique idempotencyKey |
| Job execution history foundation | Done | job_executions table/entity |
| Job logs foundation | Done | job_logs table/entity and creation log |
| Job list API | Done | GET /api/jobs?projectId=... |
| Job status filter API | Done | GET /api/jobs?projectId=...&status=QUEUED |
| Job detail API | Done | GET /api/jobs/{jobId} |
| Queue statistics from real jobs | Done | GET /api/queues/{queueId}/stats |

### Phase 3 Runtime Verification

The backend was compiled successfully, Flyway migrations V5 to V8 were applied, and all major job APIs were tested using authenticated curl requests.


## Phase 5 Completion Update — Scheduler Engine

| Requirement | Implementation Status | Evidence |
|---|---:|---|
| Enable background scheduling | Done | @EnableScheduling added in QueueForgeApplication |
| Scheduled job promotion | Done | SCHEDULED jobs become QUEUED when runAt is due |
| Retry job promotion | Done | RETRYING jobs become QUEUED after retry delay |
| Retry delay scanner | Done | JobSchedulerService scans due RETRYING jobs |
| Recurring cron generator | Done | Active scheduled_jobs generate RECURRING jobs |
| Cron next run update | Done | lastRunAt and nextRunAt updated after generation |
| Runtime scheduled promotion test | Done | Scheduled job moved from SCHEDULED to QUEUED |
| Runtime retry promotion test | Done | RETRYING job moved back to QUEUED |
| Runtime recurring generation test | Done | RECURRING job created with QUEUED status |

### Phase 5 Runtime Verification

The scheduler engine was verified using authenticated API calls and PostgreSQL checks. Scheduled jobs, retrying jobs, and recurring cron definitions were successfully processed by background scheduler tasks.


## Phase 6 Completion Update — Monitoring and Metrics API

| Requirement | Implementation Status | Evidence |
|---|---:|---|
| Dashboard metrics DTO | Done | DashboardMetricsResponse |
| Total queues count | Done | jobQueueRepository.count() |
| Total jobs count | Done | jobRepository.count() |
| Job status metrics | Done | countByStatus for QUEUED, SCHEDULED, RUNNING, COMPLETED, FAILED, RETRYING, DEAD_LETTER |
| Worker metrics | Done | total, ONLINE, DRAINING, OFFLINE worker counts |
| Metrics service | Done | MetricsService |
| Metrics REST API | Done | GET /api/metrics/dashboard |
| Runtime verification | Done | Authenticated curl request returned dashboard metrics |

### Phase 6 Runtime Verification

The monitoring API was verified using an authenticated request to GET /api/metrics/dashboard. The response includes queue, job lifecycle, retry, DLQ, and worker status metrics required for the dashboard.


## Phase 6 Completion Update — Monitoring and Metrics API

| Requirement | Implementation Status | Evidence |
|---|---:|---|
| Dashboard metrics DTO | Done | DashboardMetricsResponse |
| Total queues count | Done | jobQueueRepository.count() |
| Total jobs count | Done | jobRepository.count() |
| Job status metrics | Done | countByStatus for QUEUED, SCHEDULED, RUNNING, COMPLETED, FAILED, RETRYING, DEAD_LETTER |
| Worker metrics | Done | total, ONLINE, DRAINING, OFFLINE worker counts |
| Metrics service | Done | MetricsService |
| Metrics REST API | Done | GET /api/metrics/dashboard |
| Runtime verification | Done | Authenticated curl request returned dashboard metrics |

### Phase 6 Runtime Verification

The monitoring API was verified using an authenticated request to GET /api/metrics/dashboard. The response includes queue, job lifecycle, retry, DLQ, and worker status metrics required for the dashboard.
