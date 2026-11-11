# 📓 Project Journal & Challenges (4mulaSukoon)

This document tracks the real-world challenges faced during the development of 4mulaSukoon and how they were resolved. It serves as a proof of engineering thought process.

## 1. API & Serialization Challenges

| Challenge | Root Cause | Solution | Interview Explanation |
| :--- | :--- | :--- | :--- |
| **500 Internal Server Error** on `/api/jobs` | Used manual `new ObjectMapper()` which doesn't register Java 8 `LocalDateTime` module by default. | Removed manual instantiation and used Spring Boot's dependency-injected `ObjectMapper`. | "I faced a serialization issue with Java 8 Date/Time. I realized Spring provides a pre-configured Jackson mapper, so I used dependency injection instead of manual instantiation." |
| **400 Bad Request** on `/api/jobs` | Postman sent `Idempotency-Key` in Query Params instead of Headers. | Moved it to Headers and mapped it using `@RequestHeader` in the Controller. | "The API validation failed because the custom header was sent as a query parameter. It taught me the importance of strict API contract testing." |

## 2. Testing Strategy
- **Unit Testing (TDD):** For the `AllocationEngine`, I wrote JUnit 5 tests first. Created dummy workers (Active vs Inactive) to prove that the algorithm correctly prioritizes the inactive worker to ensure fair job distribution.
- **Integration Testing (Planned):** Will use Testcontainers to prove `SELECT FOR UPDATE` row-level locking works under high concurrency.

## 3. Upcoming Sprints
- **Sprint 4:** Spring Security & JWT.
- **Sprint 5:** External APIs (Maps for distance, Weather).

## 4. Environment & Infrastructure Challenges
- **Challenge:** Spring Boot app failed to start with `Connection refused` error.
    - *Root Cause:* The Docker containers (PostgreSQL/Redis) were not running in the background (likely due to a system restart).
    - *Solution:* Ran `docker-compose up -d` to spin up the infrastructure before starting the app.
    - *Interview Learning:* Learned the importance of environment parity and how Spring Boot's `HikariCP` fails fast if the primary database is unreachable during Flyway migrations.

## 5. Concurrency & Locking (The Core Challenge)
- **Challenge:** How to prevent double-booking when 10 workers press "Accept Job" at the exact same millisecond? (Race Condition).
- **Solution:** Implemented **Pessimistic Locking** using `@Lock(LockModeType.PESSIMISTIC_WRITE)` in `JobRepository`. In PostgreSQL, this translates to a `SELECT ... FOR NO KEY UPDATE` query, which locks the row until the transaction completes.
- **Testing Infrastructure:** Used **Testcontainers** to spin up a real PostgreSQL and Redis instance in Docker during tests, ensuring 100% environment parity with production.
- **Proof:** Wrote a concurrency test using `ExecutorService` and `CountDownLatch` to spawn 10 threads simultaneously. Result: **Exactly 1 Success, 9 Failures**. Database integrity maintained perfectly.

## 6. Spring Security & JWT Implementation
- **Challenge:** Securing APIs so that a user can only book jobs for themselves, and workers can only accept jobs as themselves.
- **Solution:** Integrated Spring Security 6 and JWT. Configured a `SecurityFilterChain` to permit only public routes (like registration) and secure the rest. Added a `JwtService` to generate and validate tokens.
- **Interview Note:** Emphasized the use of `SessionCreationPolicy.STATELESS` as JWT doesn't require server-side sessions, making the app highly scalable.

## 7. Database Migrations (Flyway Checksum Issue)
- **Challenge:** App failed to start with `FlywayValidateException: Migration checksum mismatch`.
- **Root Cause:** We modified the `V1__init_schema.sql` file (added WORKER role) after it had already been applied to the local database. Flyway tracks file checksums to prevent database corruption.
- **Solution:** For local development, we reset the database using `docker-compose down -v` and restarted it.
- **Interview Learning:** In production, you should NEVER modify an already applied migration file. Instead, you should create a new `V2__add_worker_role.sql` migration file to alter the schema.