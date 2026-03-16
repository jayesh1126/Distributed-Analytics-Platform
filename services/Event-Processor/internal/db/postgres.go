package db

import (
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
)

func NewPostgresPool() (*pgxpool.Pool, error) {
	connStr := "postgresql://user:password@localhost:5432/eventdb"
	return pgxpool.New(context.Background(), connStr)
}
