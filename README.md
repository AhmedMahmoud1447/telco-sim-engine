# Telco SIM Provisioning & Dynamic Policy Enforcement Engine
A high-performance, event-driven telecom engine built with Spring Boot & Java. Features real-time SIM provisioning, live network telemetry, and automated policy enforcement using Apache Kafka, Redis Distributed Locks, and JWT-secured Spring Cloud Gateway.

## Project Scope & Purpose (Disclaimer)
**This project is a Proof of Concept (PoC) and an educational architectural prototype.**
It is NOT a complete, commercial-grade telecom production system.

## 🏗️ Architectural Overview
<img width="1536" height="1024" alt="ChatGPT Image 26 يونيو 2026، 01_16_30 ص (1)" src="https://github.com/user-attachments/assets/991e2544-017c-4375-8282-9502eed1e5c9" />

## Architecture Overview

This project implements an Event-Driven Architecture (EDA) with the following main components:

- **API Gateway**: Spring Cloud Gateway acts as the single entry point for external clients. It routes requests to downstream services and enforces cross-cutting concerns (CORS, rate-limiting, global filters).
- **Eureka Server**: Service discovery for all microservices; services register themselves to enable dynamic routing and resilience.
- **Auth Service (JWT)**: Responsible for authenticating users and issuing JWT tokens. All protected microservices validate JWT bearer tokens on incoming requests.
- **SIM Provisioning Service**: Primary domain service that manages SIM lifecycle (activate, deactivate, profile metadata). Persistence: PostgreSQL for durable SIM records. Concurrency control: uses Redis-based distributed lock for safe concurrent provisioning/activation updates.
- **Telemetry Service**: Ingests network usage telemetry (e.g., bytes, sessions, timestamps) from the network elements or clients. Publishes usage events to Kafka for downstream processing.
- **Policy Quota Service**: Subscribes to telemetry events from Kafka and maintains per-SIM quotas in Redis (fast in-memory checks and atomic decrements). When usage causes a quota to be exhausted, emits a quota event to Kafka.
- **Auto-Healing Service**: Subscribes to quota events and executes automated remediation actions (e.g., throttle, notify, temporary block, or provision emergency quota). Can also publish events back to Kafka to restart workflows or update SIM state.
- **Apache Kafka (Event Bus)**: Backbone for asynchronous, decoupled communication among services. Typical topics used in this project: `sim-activated-topic`, `network-usage-topic`, `quota-exhausted-topic`.

High-level flow:
1. Client authenticates with **Auth Service** and receives a JWT.
2. Client calls **SIM Provisioning Service** via **API Gateway** to activate a SIM (JWT Bearer required). A successful activation may publish an event to `sim-activated-topic`.
3. Telemetry data is sent via the **API Gateway** to the **Telemetry Service**, which publishes usage events to the `network-usage-topic` on Kafka.
4. **Policy Quota Service** consumes messages from `network-usage-topic`, looks up the SIM quota in Redis, and atomically deducts usage (for example using Redis DECRBY or a Lua script).
5. If quota reaches zero or below, **Policy Quota Service** emits an event to `quota-exhausted-topic`.
6. **Auto-Healing Service** consumes events from `quota-exhausted-topic` and performs configured automated remediation actions (e.g., throttle, notify, or provision emergency quota).
7. All actions and state transitions are persisted or cached as appropriate (PostgreSQL for canonical SIM data; Redis for locks and fast quota counters).

Security model:
- **Authentication**: JWT tokens issued by **Auth Service**.
- **Authorization**: Gateway and services verify JWT and apply role-or-scope checks.
- Sensitive operations (provisioning, policy changes) are protected by tokens and may require admin roles.

Resiliency:
- Kafka decouples producers and consumers for resiliency and backpressure.
- Redis is used for atomic counters and distributed locks to avoid race conditions.
- Services register with Eureka for load balancing and failover.

---

## Service Port Grid

| Service / Component        | Port | Notes |
|---------------------------:|:----:|:------|
| API Gateway               | 8080 | Gateway entrypoint (routes to services) |
| Eureka Server             | 8761 | Service discovery UI: `http://localhost:8761` |
| Auth Service              | 8085 | Public (issue JWTs) |
| SIM Provisioning Service  | 8081 | Protected by JWT |
| Telemetry Service         | 8082 | Protected by JWT (ingest endpoint) |
| Policy Quota Service      | 8083 | Protected by JWT (quota admin & metrics) |
| Auto-Healing Service      | 8084 | Protected by JWT (remediation workflows) |
| PostgreSQL                | 5432 | Canonical SIM data storage |
| Redis                     | 6379 | Distributed locks, quota counters, cache |
| Kafka                     | 9092 | Event bus for telemetry/quota/healing events |

---

## Deployment Steps

1. Build all microservices (maven wrapper or mvn):
```powershell
mvn clean package -DskipTests
```

2. Spin up infrastructure (PostgreSQL, Redis, Kafka) and applications using Docker Compose:
```powershell
docker compose up --build -d
```

3. Verify Eureka UI and registered services:
- Open: http://localhost:8761

Notes:
- Use `docker compose logs -f` or `docker compose ps` to inspect service logs and health.
- If you modify configuration or code, re-run the `mvn` build and then `docker compose up --build -d`.

---
