# QueueForge Demo Script

## 1. Project Introduction

QueueForge is a full-stack distributed job scheduler platform. It allows users to create queues, schedule jobs, register workers, atomically claim jobs, complete jobs, fail jobs, retry jobs and monitor everything through a live dashboard.

The system is built using:
- Spring Boot backend
- PostgreSQL database
- React frontend
- Docker-based database setup
- JWT authentication

## 2. Login

Use:

Email: vaidehi@example.com  
Password: password123

After login, the dashboard opens with live metrics.

## 3. Dashboard Overview

The dashboard contains:
- Live metrics
- Action Center
- Job Gallery
- Worker Garden
- Queue Rooms
- Recovery Shelf / DLQ

## 4. Action Center Demo Flow

Run the following actions:

1. Register Worker  
2. Immediate Job  
3. Claim Job  
4. Complete Flow  
5. Fail / Retry Flow  
6. Batch Jobs  
7. Pause Queue  
8. Resume Queue  

## 5. What Each Action Proves

### Register Worker
Shows worker node registration.

### Immediate Job
Creates a job that is ready for execution immediately.

### Claim Job
Shows atomic worker claiming. QueueForge prevents duplicate claiming using PostgreSQL locking.

### Complete Flow
Shows full lifecycle:

create → claim → start → complete

### Fail / Retry Flow
Shows failure handling and retry/DLQ path.

### Batch Jobs
Shows multiple jobs being created in one request.

### Pause / Resume Queue
Shows operational queue control.

## 6. Closing Explanation

QueueForge is not just a UI. It is a working backend system with job scheduling, worker orchestration, retry handling, DLQ, scheduler logic, metrics and a polished React dashboard.
