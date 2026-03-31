package db

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

type AnalyticsRepository struct {
	Pool *pgxpool.Pool
}

func NewAnalyticsRepository(pool *pgxpool.Pool) *AnalyticsRepository {
	return &AnalyticsRepository{Pool: pool}
}

func (r *AnalyticsRepository) MarkEventProcessed(ctx context.Context, eventID string, duration int) error {
	_, err := r.Pool.Exec(ctx,
		`INSERT INTO analytics_events 
		(event_id, processed_at, processing_time_ms, duration) 
		VALUES ($1, NOW(), $2, 'processed')`,
		eventID, duration)
	return err
}

func (r *AnalyticsRepository) IsProcessed(ctx context.Context, eventID string) (bool, error) {
	var exists bool
	err := r.Pool.QueryRow(ctx, `SELECT EXISTS(SELECT 1 FROM analytics_events WHERE event_id = $1)`, eventID).Scan(&exists)
	return exists, err
}

func (r *AnalyticsRepository) IncrementMetric(ctx context.Context, name string, dimension string, value float64) error {
	_, err := r.Pool.Exec(ctx,
		`INSERT INTO analytics_metrics (metric_name, dimension, value, timestamp)
		VALUES ($1, $2, $3, NOW())`,
		name, dimension, value)
	return err
}
