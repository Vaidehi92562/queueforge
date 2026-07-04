# QueueForge Design Decisions

## Overview

This document explains the major design decisions, trade-offs, and engineering reasoning behind QueueForge.

The goal of QueueForge is not only to implement features, but to demonstrate production-style backend engineering, database design, concurrency control, reliability, and maintainability.

## 1. PostgreSQL as the Primary Database

### Decision

QueueForge uses PostgreSQL as the primary durable storage layer.

### Reason

The assignment specifically expects an efficient relational schema. PostgreSQL is a strong choice because it supports:

- Relational modeling
- Foreign keys
- Transactions
- Indexes
- JSONB payload storage
- Row-level locking
- FOR UPDATE SKIP LOCKED

### Trade-off

Using PostgreSQL for job scheduling is simpler and easier to explain than introducing external queues like Kafka, RabbitMQ, or Redis.

External queues can be faster for high-scale messaging, but PostgreSQL gives strong consistency and makes the database design evaluation stronger.

## 2. Atomic Job Claiming with FOR UPDATE SKIP LOCKED

### Decision

Workers claim jobs using PostgreSQL row-level locking with FOR UPDATE SKIP LOCKED.

### Reason

The biggest reliability risk is duplicate job execution. If multiple workers poll at the same time, the same job must not be executed twice.

FOR UPDATE SKIP LOCKED allows one worker to lock a job row while other workers skip that locked row and claim another job.

### Trade-off

This approach is excellent for an assignment and medium-scale systems. At very high scale, a dedicated queueing system may be more suitable.

## 3. Separate Worker Service

### Decision

Job execution is handled by worker instances instead of the API server.

### Reason

This separation improves system design because:

- API server handles user requests
- Worker service handles background execution
- Multiple workers can run in parallel
- Worker failures do not directly bring down the API server

### Trade-off

This adds complexity because workers need registration, heartbeat, concurrency handling, and graceful shutdown.

## 4. Scheduler Service for Delayed and Recurring Jobs

### Decision

A scheduler service is responsible for activating delayed, scheduled, and recurring jobs.

### Reason

This keeps worker logic clean. Workers only claim jobs that are ready to run.

Scheduler responsibilities include:

- Moving due delayed jobs to QUEUED
- Creating job instances from recurring cron definitions
- Updating next_run_at for recurring jobs

### Trade-off

A separate scheduler adds another component, but makes the architecture cleaner and easier to reason about.

## 5. Normalized Relational Schema

### Decision

QueueForge separates users, organizations, projects, queues, jobs, executions, logs, workers, retry policies, and dead letter entries into separate tables.

### Reason

Normalization improves:

- Data consistency
- Query clarity
- Maintainability
- Foreign key integrity
- ER diagram quality

### Trade-off

More tables mean more joins, but the design is cleaner and better for evaluation.

## 6. JSONB for Job Payloads

### Decision

Job payloads are stored as JSONB in PostgreSQL.

### Reason

Different jobs can have different payload structures. JSONB allows flexible payload storage while still keeping the main schema relational.

Example:

- Email job payload
- Report generation payload
- Payment processing payload
- Notification payload

### Trade-off

JSONB is flexible, but payload-level validation must be handled in the application layer.

## 7. Retry Policies as a Separate Table

### Decision

Retry policies are stored separately and linked to queues.

### Reason

This avoids repeating retry settings for every job and allows different queues to use different retry behavior.

Supported strategies:

- Fixed delay
- Linear backoff
- Exponential backoff

### Trade-off

This adds one more relation, but keeps retry configuration reusable and clean.

## 8. Dead Letter Queue

### Decision

Jobs that fail after maximum retry attempts are moved to a Dead Letter Queue.

### Reason

In production systems, failed jobs should not disappear. The Dead Letter Queue helps users inspect permanent failures and manually requeue jobs.

### Trade-off

This requires extra APIs and UI screens, but it greatly improves reliability and observability.

## 9. Worker Heartbeats

### Decision

Each worker sends periodic heartbeats.

### Reason

Heartbeats allow the system to detect:

- Active workers
- Offline workers
- Stale workers
- Worker health

This is important for the dashboard and reliability evaluation.

### Trade-off

Heartbeat writes add database activity, but they provide important operational visibility.

## 10. Queue-Level Concurrency Limits

### Decision

Each queue has a max_concurrency setting.

### Reason

This prevents one queue from overwhelming the system. For example, an email queue may allow 10 parallel jobs, while a payment queue may allow only 2.

### Trade-off

Concurrency control adds complexity, but it shows deeper backend engineering.

## 11. JWT Authentication

### Decision

QueueForge uses JWT authentication.

### Reason

JWT is simple, stateless, and commonly used for REST APIs.

### Trade-off

JWT tokens need careful expiration and validation handling. For this assignment, JWT provides a clean and practical authentication approach.

## 12. Role-Based Access Control

### Decision

The system supports roles such as ADMIN, DEVELOPER, and VIEWER.

### Reason

This covers the bonus requirement for role-based access control and makes the platform more realistic.

### Trade-off

RBAC adds authorization checks, but improves security and production quality.

## 13. Polling First, WebSocket as Bonus

### Decision

The dashboard will initially use polling for live updates. WebSocket support can be added as a bonus.

### Reason

Polling is simpler, reliable, and enough for monitoring dashboards.

### Trade-off

WebSockets provide real-time updates, but add more complexity. Polling first reduces implementation risk.

## 14. Docker Compose for Local Setup

### Decision

QueueForge will use Docker Compose for local development.

### Reason

Docker Compose allows one-command setup for:

- PostgreSQL
- Backend
- Frontend
- Worker instances

### Trade-off

Docker adds setup complexity initially, but makes final evaluation smoother.

## 15. Testing Critical Flows

### Decision

Testing will focus on critical reliability behavior rather than only simple CRUD tests.

Important tests include:

- Atomic job claiming
- Retry calculation
- Dead Letter Queue movement
- Worker heartbeat update
- Job creation validation
- Authentication flow

### Reason

The assignment values engineering quality, reliability, and concurrency. These tests directly support that goal.

## Final Design Goal

QueueForge is designed to prioritize reliability, correctness, observability, and maintainability over simply adding many surface-level features.
