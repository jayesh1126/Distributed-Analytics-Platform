package service

import (
	"Event-Processor/internal/analytics"
	"Event-Processor/internal/db"
	"Event-Processor/internal/idempotency"
)

type EventService struct {
	processor   *analytics.Processor
	idempotency *idempotency.Checker
}

func NewEventService(repo *db.AnalyticsRepository) *EventService {
    return &EventService{
        processor:   analytics.NewProcessor(repo.Pool()),
        idempotency: idempotency.NewChecker(repo),
    }
}