package idempotency

import (
	"context"

	"Event-Processor/internal/db"
)

type Checker struct {
	repo *db.AnalyticsRepository
}

func NewChecker(repo *db.AnalyticsRepository) *Checker {
	return &Checker{repo: repo}
}

func (c *Checker) IsProcessed(ctx context.Context, eventID string) (bool, error) {
	return c.repo.IsProcessed(ctx, eventID)
}

func (c *Checker) MarkProcessed(ctx context.Context, eventID string, durationMs int) error {
	return c.repo.MarkEventProcessed(ctx, eventID, durationMs)
}
