package com.Events_service.service;

import com.Events_service.dto.EventResponseDTO;
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
    private final KafkaTemplate<String, EventResponseDTO> kafkaTemplate;

    /**
     * Sends an event to the specified Kafka topic.
     *
     * @param topic The name of the Kafka topic to send the event to.
     * @param event The event data to be sent.
     */
    public void sendEvent(String topic, EventResponseDTO event) {
        CompletableFuture<SendResult<String, EventResponseDTO>> future = kafkaTemplate.send(topic, event.id().toString(), event);

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
