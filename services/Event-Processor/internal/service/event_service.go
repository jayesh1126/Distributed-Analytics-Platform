package service

import (
	"Event-Processor/internal/analytics"
	"Event-Processor/internal/db"
	"Event-Processor/internal/idempotency"
	"Event-Processor/internal/model"
	"context"
	"time"
)

type EventService struct {
	processor   *analytics.Processor
	idempotency *idempotency.Checker
}

func NewEventService(repo *db.AnalyticsRepository) *EventService {
	return &EventService{
		processor:   analytics.NewProcessor(repo.Pool),
		idempotency: idempotency.NewChecker(repo),
	}
}

func (s *EventService) HandleEvent(ctx context.Context, event model.Event) error {
	// for now, OrderId as unique
	eventID := event.OrderId
	if eventID == "" {
		// fallback or create synthetic ID
		eventID = event.CustomerId + ":" + event.EventType
	}

	alreadyProcessed, err := s.idempotency.IsProcessed(ctx, eventID)
	if err != nil {
		return err
	}
	if alreadyProcessed {
		return nil
	}

	start := time.Now()
	if err := s.processor.Process(ctx, event); err != nil {
		return err
	}

	durationMs := int(time.Since(start).Milliseconds())
	return s.idempotency.MarkProcessed(ctx, eventID, durationMs)
}
