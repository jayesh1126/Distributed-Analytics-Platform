-- analytics_events: used by your EventService for idempotency
CREATE TABLE IF NOT EXISTS analytics_events (
    event_id TEXT PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL,
    processing_time_ms INTEGER,
    duration TEXT
);

-- analytics_metrics: used by AnalyticsRepository.IncrementMetric
CREATE TABLE IF NOT EXISTS analytics_metrics (
    metric_name TEXT NOT NULL,
    dimension   TEXT NOT NULL,
    value       NUMERIC NOT NULL,
    timestamp   TIMESTAMP NOT NULL
);

-- product_sales: product-level sales aggregation
CREATE TABLE IF NOT EXISTS product_sales (
    product_id UUID PRIMARY KEY,
    units_sold INT NOT NULL,
    revenue    NUMERIC NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- inventory_alerts: out-of-stock or low-inventory alerts
CREATE TABLE IF NOT EXISTS inventory_alerts (
    product_id UUID NOT NULL,
    status     TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- order_summary: per-order aggregates
CREATE TABLE IF NOT EXISTS order_summary (
    order_id     UUID PRIMARY KEY,
    total_amount NUMERIC NOT NULL,
    item_count   INT NOT NULL,
    created_at   TIMESTAMP NOT NULL
);