# 📝 Interview Defense & Architecture Notes

Welcome to the engineering portfolio of **4mulaSukoon**. This document serves as a quick summary of the project's architecture, core challenges solved, and links to deeper documentation.

## 1. Elevator Pitch
"I am building **4mulaSukoon**, an on-demand home maintenance platform. The core engineering challenge is building a highly concurrent backend using Spring Boot 3, PostgreSQL, and Redis to ensure no double-booking and no double-payments occur during network retries. We focus on fair worker allocation and maintaining a digital health record for every home."

## 2. Tech Stack & Architecture
- **Backend:** Java 21, Spring Boot 3.2.5
- **Database:** PostgreSQL 16 (with Flyway for migrations)
- **Caching & Locks:** Redis 7
- **Testing:** JUnit 5, Testcontainers (Planned for Concurrency)
- **Design Pattern:** Modular Monolith with Layered Architecture (Controller -> Service -> Repository -> Entity).

## 3. Core Engineering Highlights (What makes this different?)
1. **Idempotency (Preventing Double Booking):** Implemented using Redis `setIfAbsent` with a custom `Idempotency-Key` header.
2. **Fair Allocation Engine:** A custom scoring algorithm based on Worker Inactivity, Remaining Quota, Rating, and Proximity.
3. **Race Condition Prevention (Upcoming):** Using PostgreSQL `SELECT ... FOR UPDATE` to ensure 10 concurrent workers cannot accept the same job.

## 4. Documentation Links
- 📖 **[Read the Project Journal (Challenges & Solutions)](PROJECT_JOURNAL.md)** - *Must read for real-world debugging insights.*
- 🏗️ **[System Design & ER Diagrams (Notion)](https://miniature-engine-56d.notion.site/4mulaSukoon-Project-Documentation-40f9b6f566fb801b8cd2daa8b7dd0f60)** - *Full database schema and class diagrams.*

## 5. How to Run Locally
1. Run `docker-compose up -d` to start Postgres and Redis.
2. Run `mvn spring-boot:run` to start the Spring Boot App.
