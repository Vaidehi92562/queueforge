# QueueForge API Documentation

## Overview

QueueForge exposes clean REST APIs for authentication, projects, queues, jobs, workers, dead letter queue entries, logs, and metrics.

All secured APIs require a JWT token in the Authorization header.

Example:

Authorization: Bearer <token>

## API Response Format

All APIs will follow a consistent response structure.

Success response:

{
  "success": true,
  "message": "Request completed successfully",
  "data": {}
}

Error response:

{
  "success": false,
  "message": "Validation failed",
  "errors": []
}

## 1. Authentication APIs

### Register User

POST /api/auth/register

Purpose:

Create a new user account.

Request body:

{
  "name": "Vaidehi Mishra",
  "email": "vaidehi@example.com",
  "password": "password123"
}

### Login User

POST /api/auth/login

Purpose:

Authenticate user and return JWT token.

Request body:

{
  "email": "vaidehi@example.com",
  "password": "password123"
}

Response:

{
  "token": "jwt-token"
}

## 2. Project APIs

### Create Project

POST /api/projects

Purpose:

Create a project under an organization.

### Get Projects

GET /api/projects

Purpose:

Fetch all projects accessible to the logged-in user.

### Get Project By ID

GET /api/projects/{projectId}

Purpose:

Fetch project details.

## 3. Queue APIs

### Create Queue

POST /api/queues

Purpose:

Create a queue inside a project.

Request body:

{
  "projectId": 1,
  "name": "email-queue",
  "priority": 10,
  "maxConcurrency": 5,
  "rateLimitPerMinute": 100,
  "retryPolicyId": 1
}

### Get Queues

GET /api/queues?projectId=1

Purpose:

Fetch queues for a project.

### Pause Queue

PATCH /api/queues/{queueId}/pause

Purpose:

Pause a queue so workers do not claim jobs from it.

### Resume Queue

PATCH /api/queues/{queueId}/resume

Purpose:

Resume a paused queue.

### Get Queue Stats

GET /api/queues/{queueId}/stats

Purpose:

Fetch queue-level statistics.

## 4. Retry Policy APIs

### Create Retry Policy

POST /api/retry-policies

Purpose:

Create retry configuration.

Request body:

{
  "name": "Exponential Retry",
  "strategy": "EXPONENTIAL_BACKOFF",
  "maxAttempts": 3,
  "baseDelaySeconds": 10,
  "maxDelaySeconds": 300
}

### Get Retry Policies

GET /api/retry-policies

Purpose:

Fetch retry policies.

## 5. Job APIs

### Create Immediate Job

POST /api/jobs

Purpose:

Create a job that can run immediately.

Request body:

{
  "queueId": 1,
  "type": "IMMEDIATE",
  "payload": {
    "email": "user@example.com",
    "template": "WELCOME"
  },
  "priority": 5,
  "idempotencyKey": "unique-key-123"
}

### Create Delayed Job

POST /api/jobs/delayed

Purpose:

Create a job that runs at a future timestamp.

Request body:

{
  "queueId": 1,
  "payload": {},
  "priority": 5,
  "runAt": "2026-07-04T10:30:00"
}

### Create Scheduled Job

POST /api/jobs/scheduled

Purpose:

Create a scheduled job definition.

### Create Recurring Job

POST /api/jobs/recurring

Purpose:

Create a recurring cron job.

Request body:

{
  "queueId": 1,
  "name": "daily-report-job",
  "cronExpression": "0 0 9 * * *",
  "payloadTemplate": {}
}

### Create Batch Jobs

POST /api/jobs/batch

Purpose:

Create multiple jobs in one request.

### Get Jobs

GET /api/jobs?projectId=1&status=QUEUED&page=0&size=20

Purpose:

Fetch jobs with pagination and filtering.

Supported filters:

- projectId
- queueId
- status
- type
- createdFrom
- createdTo

### Get Job Details

GET /api/jobs/{jobId}

Purpose:

Fetch one job with execution history and logs.

### Retry Failed Job

POST /api/jobs/{jobId}/retry

Purpose:

Retry a failed or dead letter job.

### Cancel Job

POST /api/jobs/{jobId}/cancel

Purpose:

Cancel a queued or scheduled job.

## 6. Worker APIs

### Register Worker

POST /api/workers/register

Purpose:

Register a worker instance.

### Send Worker Heartbeat

POST /api/workers/{workerId}/heartbeat

Purpose:

Update worker heartbeat timestamp and running job count.

### Get Workers

GET /api/workers

Purpose:

Fetch all worker instances and their status.

### Get Worker Details

GET /api/workers/{workerId}

Purpose:

Fetch worker details and assigned jobs.

## 7. Dead Letter Queue APIs

### Get Dead Letter Queue Entries

GET /api/dead-letter?projectId=1&page=0&size=20

Purpose:

Fetch permanently failed jobs.

### Requeue Dead Letter Job

POST /api/dead-letter/{entryId}/requeue

Purpose:

Move a dead letter job back to QUEUED state.

## 8. Job Execution APIs

### Get Execution History

GET /api/jobs/{jobId}/executions

Purpose:

Fetch execution attempts for a job.

### Get Job Logs

GET /api/jobs/{jobId}/logs

Purpose:

Fetch logs for a job.

## 9. Metrics APIs

### Get Overview Metrics

GET /api/metrics/overview

Purpose:

Fetch dashboard metrics.

Metrics returned:

- totalJobs
- queuedJobs
- runningJobs
- completedJobs
- failedJobs
- deadLetterJobs
- activeWorkers
- throughputPerMinute
- failureRate

### Get Queue Metrics

GET /api/metrics/queues/{queueId}

Purpose:

Fetch metrics for one queue.

### Get Worker Metrics

GET /api/metrics/workers

Purpose:

Fetch worker health and execution metrics.

## 10. API Design Principles

QueueForge APIs follow these principles:

- RESTful resource naming
- JWT authentication
- Role-based access control
- Input validation
- Pagination for list APIs
- Filtering for job explorer APIs
- Structured error responses
- Clear HTTP status codes
- Swagger/OpenAPI documentation
- Idempotency support for job creation

## 11. Important HTTP Status Codes

- 200 OK
- 201 Created
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 409 Conflict
- 500 Internal Server Error
