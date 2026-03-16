package com.Orders_service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
    UUID id,
    String customerId,
    List<Item> items,
    String status,
    Instant createdAt
) {}