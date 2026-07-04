# QueueForge Architecture

## Overview

QueueForge is a production-inspired distributed job scheduling platform that executes asynchronous background jobs across multiple worker instances reliably.

## Main Components

1. React Dashboard
2. Spring Boot API Server
3. PostgreSQL Database
4. Worker Service
5. Scheduler Service

## Supported Job Types

- Immediate jobs
- Delayed jobs
- Scheduled jobs
- Recurring cron jobs
- Batch jobs

## Reliability Features

- Atomic job claiming
- Queue-level concurrency limits
- Retry policies
- Worker heartbeats
- Graceful shutdown
- Dead Letter Queue
- Execution logs
- Idempotency keys
- Metrics and monitoring

## High-Level Architecture

React Dashboard talks to the Spring Boot API Server using REST APIs.

The Spring Boot API Server stores users, projects, queues, jobs, workers, logs, and metrics in PostgreSQL.

Multiple worker instances poll PostgreSQL, atomically claim jobs, execute them, update status, and send heartbeats.

The Scheduler Service activates delayed, scheduled, and recurring jobs when their execution time arrives.

## Job Lifecycle

Successful job:

QUEUED -> CLAIMED -> RUNNING -> COMPLETED

Scheduled job:

SCHEDULED -> QUEUED -> CLAIMED -> RUNNING -> COMPLETED

Retry flow:

RUNNING -> FAILED -> RETRYING -> QUEUED

Permanent failure:

FAILED -> DEAD_LETTER

Cancelled job:

QUEUED or SCHEDULED -> CANCELLED

## Concurrency Strategy

The main concurrency problem is preventing two workers from executing the same job.

QueueForge solves this using PostgreSQL row-level locking with FOR UPDATE SKIP LOCKED.

This allows multiple workers to poll jobs safely. When one worker locks and claims a job, other workers skip that locked row and claim different jobs.

## Worker Heartbeat Strategy

Each worker periodically updates its heartbeat timestamp.

Worker states:

- ONLINE
- OFFLINE
- DRAINING
- SHUTTING_DOWN

## Retry Strategy

QueueForge supports:

- Fixed delay retry
- Linear backoff retry
- Exponential backoff retry

If a job exceeds the maximum retry count, it is moved to the Dead Letter Queue.
