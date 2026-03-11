package analytics

import (
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
	"Event-Processor/internal/model"
)

func ProcessEvent(pool *pgxpool.Pool, event model.Event) error {

	if event.Type == "PurchaseCompleted" {
		_, err := pool.Exec(context.Background(),
			`INSERT INTO analytics_revenue (user_id, amount)
             VALUES ($1, $2)`,
			event.UserID, event.Amount)
		return err
	}
	return nil
}