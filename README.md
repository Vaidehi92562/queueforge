# QueueForge — Distributed Job Scheduler Control Center

QueueForge is a full-stack distributed job scheduler platform built with **Spring Boot, PostgreSQL, Docker and React**.

It allows users to create queues, schedule jobs, register workers, atomically claim jobs, complete jobs, fail jobs, retry jobs and monitor system health through a live dashboard.

QueueForge is not just a static UI. It is a working backend system with job scheduling, worker orchestration, retry handling, Dead Letter Queue support, scheduler logic, metrics and a polished React control center.

---

## Key Highlights

- JWT-based login and registration
- Automatic workspace setup after registration
- Organization and project management
- Queue creation, pause and resume
- Retry policy support
- Immediate, delayed, scheduled, recurring and batch jobs
- Worker registration and heartbeat
- Atomic job claiming using PostgreSQL locking
- Job lifecycle management
- Failure, retry and Dead Letter Queue handling
- Live metrics dashboard
- React Action Center to trigger real backend workflows

---

## Tech Stack

### Backend

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven

### Frontend

- React
- Vite
- CSS
- Fetch API
- Pastel glassmorphism dashboard UI

### Infrastructure

- Docker
- Docker Compose
- PostgreSQL container

---

## Project Structure

```text
queueforge/
├── backend/
│   ├── src/main/java/com/queueforge/
│   │   ├── auth/
│   │   ├── bootstrap/
│   │   ├── common/
│   │   ├── config/
│   │   ├── job/
│   │   ├── metrics/
│   │   ├── organization/
│   │   ├── project/
│   │   ├── queue/
│   │   ├── retry/
│   │   ├── scheduler/
│   │   ├── security/
│   │   ├── user/
│   │   └── worker/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   └── package.json
│
├── docs/
├── docker-compose.yml
├── README.md
└── .gitignore
```

---

## How to Run

Use three terminals.

---

### Terminal 1 — Start PostgreSQL

```bash
cd queueforge

open -a Docker

docker compose up -d postgres

docker compose ps

docker exec -it queueforge-postgres pg_isready -U queueforge -d queueforge
```

Expected output:

```text
accepting connections
```

---

### Terminal 2 — Start Backend

```bash
cd queueforge/backend

mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

Wait until the backend shows:

```text
Started QueueForgeApplication
```

---

### Terminal 3 — Start Frontend

```bash
cd queueforge/frontend

npm install

npm run dev
```

Frontend usually runs on:

```text
http://localhost:5173
```

Open the Vite local URL in the browser.

---

## Demo Login

```text
Email: vaidehi@example.com
Password: password123
```

New users can also register. After registration, QueueForge automatically creates:

- Personal Workspace
- Default Project
- Default Retry Policy
- default-queue

This prevents a blank dashboard for newly registered users.

---

## Core Modules

### 1. Authentication Module

Handles:

- User registration
- User login
- JWT token generation
- Secured API access

---

### 2. Bootstrap Module

After a new user registers, QueueForge automatically provisions:

- Organization
- Project
- Retry policy
- Default queue

This improves onboarding and makes the dashboard usable immediately.

---

### 3. Organization and Project Module

Organizations and projects provide multi-level workspace structure.

```text
User
 └── Organization
      └── Project
           └── Queue
                └── Jobs
```

---

### 4. Queue Module

Queues support:

- Queue creation
- Queue listing
- Pause queue
- Resume queue
- Queue statistics
- Priority
- Max concurrency
- Rate limit per minute
- Retry policy association

---

### 5. Job Module

QueueForge supports multiple job types:

- Immediate jobs
- Delayed jobs
- Scheduled jobs
- Recurring cron jobs
- Batch jobs

Each job contains:

- Queue ID
- Type
- Payload
- Priority
- Run time
- Cron expression
- Idempotency key
- Current attempt
- Max attempts
- Status

---

### 6. Worker Module

Workers can:

- Register themselves
- Send heartbeat
- Claim jobs
- Start jobs
- Complete jobs
- Fail jobs
- Shutdown gracefully

---

### 7. Scheduler Module

The scheduler engine handles:

- Scheduled job promotion
- Retrying job promotion
- Recurring job generation

---

### 8. Metrics Module

Dashboard metrics include:

- Total jobs
- Queued jobs
- Running jobs
- Completed jobs
- Retrying jobs
- Dead-letter jobs
- Worker count
- Queue count

---

## Job Lifecycle

```text
QUEUED
  ↓
CLAIMED
  ↓
RUNNING
  ↓
COMPLETED
```

---

## Failure Lifecycle

```text
RUNNING
  ↓
FAILED
  ↓
RETRYING
  ↓
QUEUED / DEAD_LETTER
```

---

## Atomic Job Claiming

QueueForge uses PostgreSQL row-level locking with:

```sql
FOR UPDATE SKIP LOCKED
```

This prevents multiple workers from claiming the same job at the same time.

This is one of the most important backend design decisions in the project because it makes the worker execution flow safe in a distributed environment.

---

## Retry and Dead Letter Queue

When a job fails:

1. If retry attempts are still available, the job moves to `RETRYING`.
2. The scheduler later promotes it back to `QUEUED`.
3. If retry attempts are exhausted, the job moves to `DEAD_LETTER`.

This ensures failed jobs are not silently lost.

---

## Main API Endpoints

### Auth

```text
POST /api/auth/register
POST /api/auth/login
```

---

### Organizations

```text
POST /api/organizations
GET  /api/organizations
```

---

### Projects

```text
POST /api/projects
GET  /api/projects?organizationId={organizationId}
```

---

### Retry Policies

```text
POST /api/retry-policies
GET  /api/retry-policies
```

---

### Queues

```text
POST  /api/queues
GET   /api/queues?projectId={projectId}
PATCH /api/queues/{queueId}/pause
PATCH /api/queues/{queueId}/resume
GET   /api/queues/{queueId}/stats
```

---

### Jobs

```text
POST /api/jobs/immediate
POST /api/jobs/delayed
POST /api/jobs/scheduled
POST /api/jobs/recurring
POST /api/jobs/batch
GET  /api/jobs?projectId={projectId}
GET  /api/jobs/{jobId}
```

---

### Workers

```text
POST  /api/workers/register
POST  /api/workers/{workerId}/heartbeat
POST  /api/workers/{workerId}/claim
POST  /api/workers/{workerId}/jobs/{jobId}/start
POST  /api/workers/{workerId}/jobs/{jobId}/complete
POST  /api/workers/{workerId}/jobs/{jobId}/fail
PATCH /api/workers/{workerId}/shutdown
GET   /api/workers
GET   /api/workers/{workerId}
```

---

### Metrics

```text
GET /api/metrics/dashboard
```

---

## Dashboard Features

The React dashboard includes:

- Pastel landing page
- Login/register page
- Live metrics dashboard
- Action Center
- Job Gallery
- Worker Garden
- Queue Rooms
- Recovery Shelf / Dead Letter Queue

---

## Action Center Demo Flow

Open the dashboard and go to **Action Center**.

Run this flow:

```text
1. Register Worker
2. Immediate Job
3. Claim Job
4. Complete Flow
5. Fail / Retry Flow
6. Batch Jobs
7. Pause Queue
8. Resume Queue
```

This proves:

- Worker registration
- Job creation
- Atomic job claiming
- Job execution lifecycle
- Retry/failure path
- Batch job creation
- Queue control
- Metrics update

---

## Testing Checklist

- User can register.
- User can login.
- New user gets default workspace.
- Dashboard metrics load correctly.
- Worker can be registered.
- Immediate job can be created.
- Worker can claim a job.
- Claimed job can be started.
- Running job can be completed.
- Failed job enters retry or DLQ path.
- Batch jobs can be created.
- Queue can be paused.
- Queue can be resumed.
- Metrics update after actions.

---

## Documentation

Additional documentation is available inside the `docs/` folder:

```text
docs/
├── architecture.md
├── api-documentation.md
├── database-design.md
├── design-decisions.md
├── requirement-tracker.md
├── testing-strategy.md
├── demo-script.md
└── final-submission-checklist.md
```

---

## Design Decisions

### Why Spring Boot?

Spring Boot provides strong support for REST APIs, security, scheduling, transaction management and database integration.

### Why PostgreSQL?

PostgreSQL provides durable storage and row-level locking, which is required for safe atomic job claiming.

### Why JWT?

JWT enables stateless authentication between frontend and backend.

### Why Automatic Workspace Provisioning?

A new user should not see an empty dashboard. QueueForge automatically creates a default organization, project, retry policy and queue after registration.

### Why Dead Letter Queue?

Jobs that fail repeatedly should not disappear. DLQ keeps failed jobs available for inspection and recovery.

---

## Final Status

QueueForge is a submission-ready full-stack distributed job scheduler platform with:

- Backend orchestration
- Database persistence
- Worker lifecycle management
- Atomic job claiming
- Retry and DLQ reliability
- Scheduler engine
- Metrics dashboard
- Polished React control center
