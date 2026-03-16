package analytics

import (
	"Event-Processor/internal/model"
	"context"
)


func (p *Processor) processOrderCreated(ctx context.Context, event model.Event) error {
    return p.repo.IncrementMetric(ctx, "ORDERS", "order_created", 1)
}
