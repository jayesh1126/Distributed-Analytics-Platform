package model

import (
	"time"
)

type Item struct {
	ProductId string `json:"productId"`
	Quantity  int    `json:"quantity"`
}

type Event struct {
	EventType  string    `json:"eventType"`
	CustomerId string    `json:"customerId"`
	OrderId    string    `json:"orderId"`
	Status     string    `json:"status"`
	Items      []Item    `json:"items"`
	CreatedAt  time.Time `json:"createdAt"`
}
