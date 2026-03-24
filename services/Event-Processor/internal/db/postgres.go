package db

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

func NewPostgresPool() (*pgxpool.Pool, error) {
	connStr := "postgresql://postgres:postgres@localhost:5432/orders"
	return pgxpool.New(context.Background(), connStr)
}
