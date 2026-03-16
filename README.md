# Distributed Order Processing & Analytics Platform

A **distributed event-driven microservices platform** that simulates a real-world **e‑commerce order processing backend**.

The system demonstrates how modern backend platforms ingest traffic, publish events process them asynchronously, and generate analytics at scale.

The project intentionally combines **Java and Go services** to showcase different runtime models and concurrency approaches.

Core technologies:

-   **Java Spring Boot** microservices
-   **Go services** (API Gateway, Event Processor, Load Generator)
-   **Kafka** event streaming
-   **PostgreSQL** data storage
-   **Docker Compose** infrastructure
-   **Concurrent event processing**
-   **Observability and metrics** 

The platform simulates the backend of a high‑traffic system similar to
an **order processing pipeline used by large e‑commerce platforms**.

------------------------------------------------------------------------

# System Architecture

The platform follows an **event-driven architecture** where services
communicate through Kafka.

High level flow:

    Load Generator (Go)
            │
            ▼
    API Gateway (Go)
            │
            ▼
    Order Service (Java - Spring Boot)
            │
            ├─ Store order in PostgreSQL
            │
            └─ Publish OrderCreated event → Kafka
                    │
                    ▼
    Kafka Topic: order-events
                    │
                    ▼
    Event Processor (Go)
            │
            ├─ Consume events
            ├─ Process concurrently
            └─ Update analytics tables
                    │
                    ▼
    PostgreSQL Analytics Database

This architecture demonstrates **decoupled services, asynchronous processing, and scalable event pipelines**.

------------------------------------------------------------------------

# Microservices

The platform currently consists of **four core services**.

------------------------------------------------------------------------

# 1. API Gateway (Go)

The **API Gateway** is the entry point to the system.

Responsibilities:

-   Request routing
-   Request logging
-   Trace ID propagation
-   Rate limiting (optional)
-   Metrics collection

The gateway forwards requests to the **Order Service**.

Example routes:

    POST /orders → Order Service
    GET /orders → Order Service

This service demonstrates **Go middleware patterns and high‑performance
request handling**.

------------------------------------------------------------------------

# 2. Order Service (Java - Spring Boot)

The **Order Service** handles order creation and persistence.

Responsibilities:

-   Accept order requests
-   Persist orders in PostgreSQL
-   Publish order events to Kafka
-   Expose APIs to retrieve orders

This service represents the **core business logic of the system**.

Run the service:

    ./gradlew bootRun

Default URL:

    http://localhost:8080

------------------------------------------------------------------------

# Order API

Base URL:

    http://localhost:8080

## Create Order

    POST /orders

Example request:

``` bash
curl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{
  "customerId": "cust-123",
  "items": [
    { "productId": "p1", "quantity": 2, "price": 10 },
    { "productId": "p2", "quantity": 1, "price": 50 }
  ]
}'
```

Example response:

``` json
{
  "orderId": "uuid",
  "status": "CREATED",
  "totalPrice": 70,
  "createdAt": "2026-03-11T22:00:00Z"
}
```

When an order is created:

1.  The order is stored in PostgreSQL
2.  An **OrderCreated event** is published to Kafka

------------------------------------------------------------------------

## Get Orders

    GET /orders

Example:

    curl http://localhost:8080/orders

Example response:

``` json
[
  {
    "orderId": "uuid",
    "customerId": "cust-123",
    "status": "CREATED",
    "totalPrice": 70,
    "createdAt": "2026-03-11T22:00:00Z"
  }
]
```

------------------------------------------------------------------------

# 3. Load Generator (Go)

The **Load Generator** simulates traffic against the system.

Its purpose is to **stress test the Order Service and Kafka pipeline**.

Capabilities:

-   Generate high request throughput
-   Simulate thousands of customers
-   Run concurrent goroutines
-   Send requests through the API Gateway

Example usage:

    go run generator.go --rate 1000 --duration 5m

This would simulate:

    1000 orders per second for 5 minutes

The generator produces realistic order payloads with randomized:

-   customer IDs
-   product selections
-   quantities

This allows testing:

-   API throughput
-   Kafka throughput
-   database load
-   processor scalability

------------------------------------------------------------------------

# 4. Event Processor (Go)

The **Event Processor** consumes order events from Kafka.

This service performs **asynchronous analytics processing**.

Responsibilities:

-   Consume events from Kafka
-   Process events concurrently using worker pools
-   Update analytics tables in PostgreSQL

Architecture:

    Kafka Consumer
          │
          ▼
    Buffered Channel
          │
          ▼
    Worker Pool (goroutines)
          │
          ▼
    Event Handlers

Example worker pattern:

    for i := 0; i < workers; i++ {
        go worker(eventChannel)
    }

This demonstrates **highly concurrent event processing using Go**.

------------------------------------------------------------------------

# Kafka Event Model

Topic:

    order-events

Example event:

``` json
{
  "eventId": "uuid",
  "eventType": "ORDER_CREATED",
  "orderId": "ord-123",
  "customerId": "cust-123",
  "totalPrice": 70,
  "timestamp": "2026-03-12T12:00:00Z"
}
```

Future event types may include:

    ORDER_CREATED
    ORDER_CANCELLED
    PAYMENT_COMPLETED
    ORDER_SHIPPED

Kafka enables:

-   asynchronous communication
-   service decoupling
-   horizontal scalability

------------------------------------------------------------------------

# Local Infrastructure

Infrastructure is provided using **Docker Compose**.

Services:

-   PostgreSQL
-   pgAdmin
-   Kafka
-   Zookeeper
-   Kafka UI

Start infrastructure:

    docker compose up -d

------------------------------------------------------------------------

# PostgreSQL Configuration

Connection settings:

    Host: localhost
    Port: 5432
    Database: orders
    User: postgres
    Password: postgres

PostgreSQL stores:

-   orders
-   analytics metrics
-   processed events

------------------------------------------------------------------------

# pgAdmin

Database UI:

    http://localhost:5050

Credentials:

    Email: admin@admin.com
    Password: admin

Add server:

    Host: postgres
    Port: 5432
    User: postgres
    Password: postgres

------------------------------------------------------------------------

# Kafka & Kafka UI

Kafka broker:

    localhost:9092

Topic:

    order-events

Kafka UI:

    http://localhost:8081

Zookeeper:

    localhost:2181

------------------------------------------------------------------------

# Analytics Database

The Event Processor updates analytics metrics.

Example table:

    sales_metrics

Example schema:

    metric_name
    dimension
    value
    updated_at

Example metrics:

    orders_total
    revenue_total
    orders_per_minute
    top_products

------------------------------------------------------------------------

# Planned Improvements

This project will evolve into a **fully observable distributed system**.

Planned additions:

### Observability

-   Prometheus metrics
-   Grafana dashboards
-   OpenTelemetry distributed tracing
-   ELK logging stack

### Idempotent Event Processing

Kafka may deliver duplicate messages.

A `processed_events` table will ensure events are processed only once.

### Kubernetes Deployment

Deploy the entire system using Kubernetes to demonstrate:

-   horizontal scaling
-   service replication
-   rolling deployments
-   container orchestration

------------------------------------------------------------------------

# What This Project Demonstrates

This repository showcases many modern backend engineering concepts:

-   Event‑driven architecture
-   Kafka streaming pipelines
-   Go concurrency patterns
-   Java Spring Boot microservices
-   Distributed system design
-   Load testing
-   Asynchronous processing
-   Containerized infrastructure

The goal is to simulate how **large distributed systems ingest, process, and analyze high volumes of events in production environments**.
