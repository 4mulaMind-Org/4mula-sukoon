# 🚀 4mulaSukoon - Project Interview Notes

## 1. Elevator Pitch
"I am building 4mulaSukoon, an on-demand home maintenance platform. The core engineering challenge is building a highly concurrent backend using Spring Boot 3, PostgreSQL, and Redis to ensure no double-booking and no double-payments occur."

## 2. Architecture & Database Design
*   **PostgreSQL:** Chosen for ACID compliance.
*   **Redis:** Used for Idempotency.
*   **ER Diagram:** Created relationships between `Users`, `Workers`, `Jobs`, and `Worker_Slots`.
*   **Class Diagram:** Designed to show how `AllocationEngine` interacts with `Worker` and `Job`.

## 3. Core Features Implemented
*   **User & Worker Registration:** Standard REST APIs to onboard users and workers.
*   **Job Creation with Idempotency:**
    *   Used Redis `setIfAbsent` to prevent duplicate job creation (network retries).
    *   Returns `409 Conflict` if a duplicate `Idempotency-Key` is sent.
*   **Fair Allocation Engine (The Core):**
    *   Implemented a scoring algorithm based on: `Inactivity + Quota + Rating - Cancellations`.
    *   Wrote Unit Tests (TDD) to verify that an inactive worker gets a higher score than an active one.

## 4. Challenges Faced & Solutions (CRITICAL FOR INTERVIEWS)
1.  **Challenge:** Faced `500 Internal Server Error` while returning Job Response.
    *   *Root Cause:* Manual `new ObjectMapper()` could not serialize `LocalDateTime`.
    *   *Solution:* Removed manual instantiation and used Spring Boot's dependency-injected `ObjectMapper`.
2.  **Challenge:** `400 Bad Request` on Idempotency test.
    *   *Root Cause:* The `Idempotency-Key` was sent as a Query Parameter instead of a Header.
    *   *Solution:* Fixed the API contract to read the key using `@RequestHeader`.

## 5. Testing
*   **Unit Testing:** Used JUnit 5 to test the `AllocationEngine` logic.

## 6. Core Features Implemented (Continued)
*   **Database Migrations:** Used Flyway to auto-create tables (`users`, `workers`, `jobs`, `worker_slots`) on app startup.
*   **Allocation Engine Integration:** Integrated the `AllocationEngine` directly into the `JobService`. When a new job is created, it fetches all available workers for that specific skill from PostgreSQL, calculates their scores, and selects the top workers.

**Interview Proof (Logs):**
> "During testing, I observed the Hibernate logs. The system inserted a new Job (ID: 2) and then executed a query `SELECT * FROM workers WHERE primary_skill = ?`. The Allocation Engine processed this data and printed: `Worker ID: 1 | Score: 363.5`. This proved end-to-end data flow and algorithm execution."

- **Race Condition Prevention:** Proven using Testcontainers where 10 concurrent threads attempted to accept the same job. Only 1 succeeded, while 9 received a 409 Conflict, ensuring zero double-booking.