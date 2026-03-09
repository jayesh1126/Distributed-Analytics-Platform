package com.Events_service.service;

import com.Events_service.dto.EventRequestDTO;
import com.Events_service.dto.EventResponseDTO;
import com.Events_service.model.Event;
import com.Events_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponseDTO createEvent(EventRequestDTO request) {

        Event event = Event.builder()
                .type(request.getType())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .createdAt(Instant.now())
                .build();

        Event saved =  eventRepository.save(event);
        return mapToResponse(saved);
    }

    public List<EventResponseDTO> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EventResponseDTO mapToResponse(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .type(event.getType())
                .userId(event.getUserId())
                .amount(event.getAmount())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
