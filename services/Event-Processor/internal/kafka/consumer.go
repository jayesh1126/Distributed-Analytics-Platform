package kafka

import (
	"context"

	"github.com/segmentio/kafka-go"
)

// Reader is perfect for consuming from a single topic and partition.
// Automatically handles reconnections and offset commits.
func newConsumer() *kafka.Reader {
	return kafka.NewReader(kafka.ReaderConfig{
		Brokers:   []string{"localhost:9092"},
		Topic:     "orders",
		Partition: 0,
		GroupID:   "order-processor-group",
	})
}

// ReadMessage reads a single message from the Kafka topic.
func ReadMessage(reader *kafka.Reader) (kafka.Message, error) {
	return reader.ReadMessage(context.Background())
}
