package kafka

import (
	"context"
	"github.com/segmentio/kafka-go"
)

func newConsumer() *kafka.Reader {
	return kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "events",
		GroupID: "event-processor-group",
	})
}

func ReadMessage(reader *kafka.Reader) (kafka.Message, error) {
	return reader.ReadMessage(context.Background())
}