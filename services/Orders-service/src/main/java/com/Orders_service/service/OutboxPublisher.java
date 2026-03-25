package com.Orders_service.service;

import com.Orders_service.model.OutboxEvent;
import com.Orders_service.repository.OutboxRepository;
import com.Orders_service.util.Mapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.Orders_service.util.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final EventProducer eventProducer;
    private final Mapper mapper;

    /**
     * Periodically publishes pending events from the outbox table to Kafka.
     * <p>
     * Flow:
     * \- fetches a batch of outbox records using a locking query to avoid concurrent processing,
     * \- maps each {@link OutboxEvent} to its corresponding EventDTO via {@link Mapper},
     * \- sends the mapped event to the appropriate Kafka topic using {@link EventProducer},
     * \- on success, marks the outbox record as "SENT" and updates the last attempt timestamp,
     * \- on failure, marks the outbox record as "FAILED", increments the retry count, and updates the last attempt timestamp.
     * <p>
     * This method is scheduled to run at a fixed delay to implement the transactional outbox pattern.
     */
    @Transactional
    @Scheduled(fixedDelay = 1000) // Check every second for new outbox events
    public void publishOutboxEvents() {
        log.info("Checking for outbox events to publish...");
        List<OutboxEvent> events = outboxRepository.lockBatch(50);
        log.info("Found {} events", events.size());

        for (OutboxEvent event : events) {

            try {
                String topic = resolveTopic(event);
                log.info("Publishing outbox event {} of type {} to topic {}", event.getId(), event.getEventType(), topic);

                eventProducer.sendEvent(topic, mapper.mapOutboxToEventDTO(event));

                event.setStatus("SENT");
                event.setLastAttemptAt(Instant.now());

            } catch (Exception e) {

                log.error("Failed to publish event {}", event.getId(), e);

                event.setStatus("FAILED");
                event.setRetryCount(
                        event.getRetryCount() == null ? 1 : event.getRetryCount() + 1
                );
                event.setLastAttemptAt(Instant.now());
            }
        }
    }

    /**
     * Resolves the Kafka topic to publish to based on the outbox event type.
     * <p>
     * Currently:
     * \- `order_created` events are sent to `ORDERS_TOPIC`
     * \- `restock_request` events are sent to `STOCK_TOPIC`
     * <p>
     * Unknown event types default to `ORDERS_TOPIC`.
     *
     * @param event the outbox event being published
     * @return the Kafka topic name to use
     */
    private String resolveTopic(OutboxEvent event) {
        String type = event.getEventType();
        if (ORDER_CREATED_EVENT.equals(type)) {
            return ORDERS_TOPIC;
        }
        if (RESTOCK_REQUEST_EVENT.equals(type)) {
            return STOCK_TOPIC;
        }
        log.warn("Unknown event_type '{}' for outbox event {}. Defaulting to ORDERS_TOPIC.", type, event.getId());
        return ORDERS_TOPIC;
    }
}
