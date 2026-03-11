package com.Events_service.service;

import com.Events_service.dto.EventRequestDTO;
import com.Events_service.dto.EventResponseDTO;
import com.Events_service.model.Event;
import com.Events_service.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.Events_service.util.Constants.EVENTS_TOPIC;

/**
 * Service responsible for handling event-related operations such as creating and retrieving events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper jsonMapper;
    private final EventProducer eventProducer;

    /**
     * Creates a new event based on the provided request data, saves it to the database, and produces it to a Kafka topic.
     *
     * @param request The event request data containing type, userId, and payload.
     * @return An EventResponseDTO representing the created event.
     */
    public EventResponseDTO createEvent(EventRequestDTO request) {
        log.info("Creating event of type {} for user {} with payload {}", request.type(), request.userId(), request.payload());
        JsonNode payloadNode = jsonMapper.valueToTree(request.payload());

        Event event = Event.builder()
                .type(request.type())
                .userId(request.userId())
                .payload(payloadNode)
                .createdAt(Instant.now())
                .build();

        Event saved =  eventRepository.save(event);
        EventResponseDTO responseDTO = mapToResponse(saved);
        eventProducer.sendEvent(EVENTS_TOPIC, responseDTO);
        return responseDTO;
    }

    /**
     * Retrieves all events from the database and maps them to a list of EventResponseDTOs.
     *
     * @return A list of EventResponseDTOs representing all events in the database.
     */
    public List<EventResponseDTO> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Maps an Event entity to an EventResponseDTO, converting the JSON payload to a Map.
     *
     * @param event The Event entity to be mapped.
     * @return An EventResponseDTO representing the provided Event entity.
     */
    private EventResponseDTO mapToResponse(Event event) {
        Map<String, Object> payloadMap = jsonMapper.convertValue(
                event.getPayload(),
                new TypeReference<Map<String, Object>>() {}
        );

        return new EventResponseDTO(
                event.getId(),
                event.getType(),
                event.getUserId(),
                payloadMap,
                event.getCreatedAt()
        );
    }
}
