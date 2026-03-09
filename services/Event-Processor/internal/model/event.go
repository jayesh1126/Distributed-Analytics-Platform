package model

import "time"

type Event struct {
	ID		string    `json:"id"`
	Type	string    `json:"type"`
	UserID	string    `json:"userId"`
	Amount	float64   `json:"amount"`
	CreatedAt	time.Time `json:"createdAt"`
}