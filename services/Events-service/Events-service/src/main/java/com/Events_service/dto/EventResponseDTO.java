package com.Events_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class EventResponseDTO {

    private UUID id;

    private String type;

    private String userId;

    private Double amount;

    private Instant createdAt;
}