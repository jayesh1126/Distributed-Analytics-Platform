package com.Orders_service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record EventDTO(
        String eventType,
        String customerId,
        String orderId,
        String status,
        List<Item> items,
        Instant createdAt
) {
}
