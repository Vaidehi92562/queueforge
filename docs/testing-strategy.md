# QueueForge Testing Strategy

## Overview

QueueForge testing focuses on correctness, reliability, concurrency, and API behavior.

The goal is not only to test simple CRUD APIs, but also to test the most important distributed scheduler flows.

## Testing Tools

The backend will use:

- JUnit
- Mockito
- Spring Boot Test
- Testcontainers
- PostgreSQL test container

The frontend will use:

- React Testing Library
- Vitest

## 1. Unit Tests

Unit tests will verify isolated business logic.

Important unit test areas:

- Retry delay calculation
- Job status transitions
- Queue pause and resume logic
- Worker status calculation
- Idempotency key validation
- Dead Letter Queue movement logic

Example retry tests:

- Fixed delay should always return same delay
- Linear backoff should increase delay linearly
- Exponential backoff should double delay
- Retry should stop after max attempts

## 2. Integration Tests

Integration tests will verify database and API behavior together.

Important integration test areas:

- User registration
- User login
- Project creation
- Queue creation
- Job creation
- Job filtering
- Retry failed job
- Dead Letter Queue requeue
- Worker heartbeat update

## 3. Concurrency Tests

Concurrency testing is the most important part of QueueForge.

The main test will verify that multiple workers cannot claim the same job.

Test idea:

- Insert multiple QUEUED jobs into PostgreSQL
- Start multiple worker threads
- Each worker tries to claim jobs at the same time
- Assert that no job is claimed by more than one worker
- Assert that all claimed jobs have unique job IDs

This validates the PostgreSQL FOR UPDATE SKIP LOCKED strategy.

## 4. Worker Tests

Worker tests will verify:

- Worker registration
- Worker heartbeat update
- Worker offline detection
- Job claiming
- Job status update from CLAIMED to RUNNING
- Job completion
- Job failure handling
- Graceful shutdown behavior

## 5. Retry and DLQ Tests

Retry tests will verify:

- Failed job moves to RETRYING
- Retry count increases
- Next run time is calculated correctly
- Job returns to QUEUED after retry delay
- Job moves to DEAD_LETTER after maximum attempts

Dead Letter Queue tests will verify:

- Final error message is stored
- Failed attempt count is stored
- Job can be manually requeued
- Requeued job returns to QUEUED state

## 6. API Tests

API tests will verify:

- Correct HTTP status codes
- Validation errors
- Unauthorized access
- Forbidden access for wrong roles
- Pagination response
- Filtering response
- Structured error response format

Important endpoints to test:

- POST /api/auth/register
- POST /api/auth/login
- POST /api/projects
- POST /api/queues
- POST /api/jobs
- GET /api/jobs
- POST /api/jobs/{jobId}/retry
- POST /api/jobs/{jobId}/cancel
- POST /api/dead-letter/{entryId}/requeue

## 7. Frontend Tests

Frontend tests will focus on important user flows:

- Login form validation
- Dashboard metrics rendering
- Queue list rendering
- Job table rendering
- Job status filter
- Failed job retry button
- Worker status display
- Dead Letter Queue page rendering

## 8. Manual Demo Test Plan

The final demo will include these flows:

1. Register and login
2. Create a project
3. Create a queue
4. Create immediate jobs
5. Create delayed jobs
6. Start multiple workers
7. Show workers claiming different jobs
8. Show completed jobs
9. Force a job failure
10. Show retry behavior
11. Show Dead Letter Queue movement
12. Requeue a failed job
13. Show dashboard metrics

## 9. Critical Flows for Evaluation

The most important tests for evaluator confidence are:

- Atomic job claiming test
- Retry backoff calculation test
- Dead Letter Queue movement test
- Worker heartbeat test
- Job lifecycle transition test
- Authentication and authorization test

## Testing Goal

QueueForge testing is designed to prove that the system is reliable under concurrent worker execution and not just a simple CRUD application.
