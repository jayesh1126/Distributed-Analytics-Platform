# Distributed-Analytics-Platform

---------------------------------------------------------------------------
# 1 - Event Service (Java Spring Boot)

A minimal **Spring Boot microservice** responsible for ingesting and
retrieving events.\
This service is the first building block of a larger distributed event
analytics platform.

The service exposes a REST API that ingests events with flexible JSON payload and persists them in **PostgreSQL** and publishes them to Kafka topic **events**.\
Infrastructure is provided locally via **Docker Compose** (Postgres +
pgAdmin).

Run the Spring Boot Event service:

``` bash
./gradlew bootRun
```

# API Endpoints

Base URL:

http://localhost:8080

## Create Event

POST /events

Example request:

``` bash
curl -X POST http://localhost:8080/events \
-H "Content-Type: application/json" \
-d '{
  "type": "PAYMENT_COMPLETED",
  "userId": "123",
  "payload": {
    "amount": 49.99,
    "currency": "USD"
  }
}'
```

Example response:

``` json
{
  "id": "uuid",
  "type": "PAYMENT_COMPLETED",
  "userId": "123",
  "payload": {
    "amount": 49.99,
    "currency": "USD"
  },
  "createdAt": "2026-03-11T22:00:00Z"
}
```

## Get Events

GET /events

``` bash
curl http://localhost:8080/events
```

Example response:

``` json
[
  {
    "id": "uuid",
    "type": "PAYMENT_COMPLETED",
    "userId": "123",
    "payload": {
      "amount": 49.99,
      "currency": "USD"
    },
    "createdAt": "2026-03-11T22:00:00Z"
  }
]
```

# API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

------------------------------------------------------------------------

# 2 - Local Infrastructure

Infrastructure is defined in **Docker Compose**.

Services:

-   PostgreSQL database
-   pgAdmin database UI
-   Kafka
-   Zookeeper
-   Kafka UI

Start infrastructure:

``` bash
docker compose up -d
```

------------------------------------------------------------------------

# 3 - PostgreSQL

Connection settings:

Host:localhost

Port:5432

Database: events

User: postgres

Password:postgres

------------------------------------------------------------------------

# 4 - pgAdmin

Web UI: http://localhost:5050

Email: admin@admin.com

Password: admin

Add server in pgAdmin:

Host: postgres / Port: 5432 / User: postgres / Password: postgres

You can now inspect the **events table** and query data directly.

------------------------------------------------------------------------

# 5 - Kafka & Kafka UI

Kafka broker: Bootstrap server: localhost:9092
Topic: events

Kafka UI: localhost:8081

Zookeeper: localhost:2181 (used internally by Kafka)

------------------------------------------------------------------------

# Next Steps

The Event Service is the first component of a larger distributed system.

Planned improvements:

### 1. Event Processor Service (Go)

A Go microservice will consume events from Kafka and generate analytics.

Kafka → Processor → Analytics Database

### 2. API Gateway

Introduce a Go-based gateway for:

-   routing
-   authentication
-   request logging
-   rate limiting

### 3. Event Generator (Load Testing)

A Go-based tool:

-   Send n events/second
-   Simulate traffic

### 4. Observability

Add production-grade observability:

-   OpenTelemetry distributed tracing
-   Prometheus metrics
-   Grafana dashboards
-   Logging via ELK stack

### 5. Frontend Dashboard

A Next.js application to visualize:

-   events
-   analytics
-   system metrics

### 6. Kubernetes Deployment

Deploy the full platform using Kubernetes:

-   service deployments
-   ingress
-   observability stack

------------------------------------------------------------------------

This service is the **foundation for a distributed event processing
platform**.
