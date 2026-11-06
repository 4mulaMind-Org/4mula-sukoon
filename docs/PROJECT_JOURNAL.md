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