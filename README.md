# 🏠 4mulaSukoon

**"India's home maintenance operating system."**

4mulaSukoon is a home maintenance operating system designed to treat home care as a relationship, not a transaction.

## 🚀 Vision
To build India's first unified home maintenance platform that provides:
- **Home Health Record:** Digital twin of the home.
- **Fair Worker Allocation:** Transparent, algorithm-based scoring.
- **Unified AMC:** One subscription for all home maintenance.
- **Idempotent Bookings:** Zero double-payments and zero double-booking.

## 🛠 Tech Stack
- **Backend:** Java 21, Spring Boot 3.2.5
- **Database:** PostgreSQL 16 (Flyway for migrations)
- **Caching & Locks:** Redis 7
- **Testing:** JUnit 5, Mockito
- **DevOps:** Docker, Docker Compose

## 🧠 Core Engineering Challenges Solved
1. **Race Conditions:** Using Pessimistic Locking (`SELECT ... FOR UPDATE`) to prevent multiple workers from accepting the same job.
2. **Idempotency:** Using Redis `setIfAbsent` to prevent double bookings during network retries.
3. **Fair Allocation Engine:** A custom scoring algorithm based on Inactivity, Quota, and Rating to ensure equal job distribution.

## 📚 Documentation
- [Project Charter](link_to_notion)
- [System Design & ER Diagram](link_to_notion)

## 🏃‍♂️ How to Run
1. `docker-compose up -d` (Starts Postgres and Redis)
2. `mvn spring-boot:run` (Starts the Spring Boot App)