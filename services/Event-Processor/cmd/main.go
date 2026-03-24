package main

import (
	"context"
	"encoding/json"
	"log"
	"os"
	"os/signal"
	"syscall"

	"Event-Processor/internal/db"
	"Event-Processor/internal/kafka"
	"Event-Processor/internal/model"
	"Event-Processor/internal/service"
)

func main() {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Cancel
	go func() {
		ch := make(chan os.Signal, 1)
		signal.Notify(ch, syscall.SIGINT, syscall.SIGTERM)
		<-ch
		cancel()
	}()

	// DB
	pool, err := db.NewPostgresPool()
	if err != nil {
		log.Fatalf("failed to connect to postgres: %v", err)
	}
	defer pool.Close()

	repo := db.NewAnalyticsRepository(pool)
	eventService := service.NewEventService(repo)

	brokers := []string{"localhost:9092"}

	orderConsumer := kafka.NewOrderConsumer(brokers, "event-processor-group")
	defer orderConsumer.Close()

	stockConsumer := kafka.NewStockConsumer(brokers, "event-processor-group")
	defer stockConsumer.Close()

	// Start goroutines per topic
	go consumeLoop(ctx, "order", orderConsumer, eventService)
	go consumeLoop(ctx, "stock", stockConsumer, eventService)

	// Block until context cancelled
	<-ctx.Done()
	log.Println("shutting down event processor")
}

func consumeLoop(
	ctx context.Context,
	topic string,
	consumer *kafka.Consumer,
	svc *service.EventService,
) {
	log.Printf("starting consumer loop for topic=%s", topic)
	for {
		msg, err := consumer.ReadMessage(ctx)
		if err != nil {
			if ctx.Err() != nil {
				return
			}
			log.Printf("error reading from topic %s: %v", topic, err)
			continue
		}

		var event model.Event
		if err := json.Unmarshal(msg.Value, &event); err != nil {
			log.Printf("failed to unmarshal event from topic %s: %v", topic, err)
			continue
		}

		if err := svc.HandleEvent(ctx, event); err != nil {
			log.Printf("failed to handle event from topic %s: %v", topic, err)
		}
	}
}
