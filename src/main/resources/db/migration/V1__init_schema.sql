CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       phone VARCHAR(15) UNIQUE NOT NULL,
                       name VARCHAR(100),
                       role VARCHAR(20) CHECK (role IN ('CUSTOMER', 'WORKER', 'ADMIN')),
                       created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE workers (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT REFERENCES users(id),
                         primary_skill VARCHAR(50),
                         skills TEXT[],
                         rating DECIMAL(3,2) DEFAULT 5.00,
                         quota_remaining INT DEFAULT 10,
                         last_job_at TIMESTAMP,
                         cancellations INT DEFAULT 0
);

CREATE TABLE jobs (
                      id BIGSERIAL PRIMARY KEY,
                      customer_id BIGINT REFERENCES users(id),
                      service_type VARCHAR(50),
                      status VARCHAR(20) DEFAULT 'REQUESTED',
                      scheduled_at TIMESTAMP,
                      worker_id BIGINT REFERENCES workers(id)
);

CREATE TABLE worker_slots (
                              id BIGSERIAL PRIMARY KEY,
                              worker_id BIGINT REFERENCES workers(id),
                              slot_start TIMESTAMP NOT NULL,
                              slot_end TIMESTAMP NOT NULL,
                              job_id BIGINT REFERENCES jobs(id),
                              UNIQUE(worker_id, slot_start)
);