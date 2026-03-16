package analytics

import (
	"Event-Processor/internal/db"
	"Event-Processor/internal/model"
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Processor struct {
	repo *db.AnalyticsRepository
}

func NewProcessor(pool *pgxpool.Pool) *Processor {
	return &Processor{
		repo: db.NewAnalyticsRepository(pool),
	}
}

func (p *Processor) Process(ctx context.Context, event model.Event) error {

	switch event.EventType {

	case "order_created":
        return p.processOrderCreated(ctx, event)
    // case "stock_updated":
    //     return p.processStockUpdated(ctx, event)
    default:
        // unknown / uninteresting event types are ignored
        return nil
    }
}
