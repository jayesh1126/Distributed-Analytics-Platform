package com.Events_service.controller;

import com.Events_service.dto.EventRequestDTO;
import com.Events_service.dto.EventResponseDTO;
import com.Events_service.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event ingestion API")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event")
    public EventResponseDTO createEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO){
        return eventService.createEvent(eventRequestDTO);
    }

    @GetMapping
    @Operation(summary = "Get all events")
    public List<EventResponseDTO> getEvents() {
        return eventService.getEvents();
    }
}
