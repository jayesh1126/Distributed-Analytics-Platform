package com.Events_service.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EventResponseDTO (
    UUID id,
    String type,
    String userId,
    Map<String, Object> payload,
    Instant createdAt
) {}