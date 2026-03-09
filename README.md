# Distributed-Analytics-Platform

---------------------------------------------------------------------------
# 1 - Event Service (Java Spring Boot)

A minimal **Spring Boot microservice** responsible for ingesting and
retrieving events.\
This service is the first building block of a larger distributed event
analytics platform.

The service exposes a REST API to create and retrieve events and
persists them in **PostgreSQL**.\
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
curl -X POST http://localhost:8080/events -H "Content-Type: application/json" -d '{
  "type": "PurchaseCompleted",
  "userId": "123",
  "amount": 49.99
}'
```

Example response:

``` json
{
  "id": "uuid",
  "type": "PurchaseCompleted",
  "userId": "123",
  "amount": 49.99,
  "createdAt": "2026-03-10T00:00:00Z"
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
    "type": "PurchaseCompleted",
    "userId": "123",
    "amount": 49.99,
    "createdAt": "2026-03-10T00:00:00Z"
  }
]
```

# API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

# Project Structure

    event-service-java
    ├── controller
    ├── service
    ├── repository
    ├── model
    ├── dto
    └── EventServiceApplication

------------------------------------------------------------------------

# 2 - Local Infrastructure

Infrastructure is defined in **Docker Compose**.

Services:

-   PostgreSQL database
-   pgAdmin database UI

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

# Next Steps

The Event Service is the first component of a larger distributed system.

Planned improvements:

### 1. Kafka Event Publishing

When events are created:

POST /events\
↓\
Store in Postgres\
↓\
Publish event to Kafka topic

### 2. Event Processor Service (Go)

A Go microservice will consume events from Kafka and generate analytics.

Kafka → Processor → Analytics Database

### 3. API Gateway

Introduce a Go-based gateway for:

-   routing
-   authentication
-   request logging
-   rate limiting

### 4. Observability

Add production-grade observability:

-   OpenTelemetry tracing
-   Prometheus metrics
-   Grafana dashboards

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
