package com.Orders_service.service;

import com.Orders_service.dto.EventDTO;
import com.Orders_service.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for producing events to Kafka topics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;

    /**
     * Sends an event to the specified Kafka topic.
     *
     * @param topic The name of the Kafka topic to send the event to.
     * @param event The event data to be sent.
     */
    public void sendEvent(String topic, EventDTO event) {
        CompletableFuture<SendResult<String, EventDTO>> future = kafkaTemplate.send(topic, event.orderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent to topic {} partition {} offset {}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error("Failed to send event to topic {}: {}", topic, ex.getMessage());
            }
        });
    }
}
