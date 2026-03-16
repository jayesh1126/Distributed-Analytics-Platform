package com.Orders_service.util;

import com.Orders_service.dto.EventDTO;
import com.Orders_service.dto.Item;
import com.Orders_service.dto.OrderResponseDTO;
import com.Orders_service.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final ObjectMapper objectMapper;

    /**
     * Maps an Order entity to an OrderResponseDTO, converting the JSON payload to a Map.
     *
     * @param order The Order entity to be mapped.
     * @return An OrderResponseDTO representing the provided Order entity.
     */
    public OrderResponseDTO mapToResponse(Order order) {
        List<Item> items = objectMapper.convertValue(
                order.getItems(),
                new TypeReference<List<Item>>() {}
        );

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                items,
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public EventDTO mapToEventWithType(OrderResponseDTO orderResponseDTO, String eventType) {
        return EventDTO.builder()
                .eventType(eventType)
                .customerId(orderResponseDTO.customerId())
                .orderId(orderResponseDTO.id().toString())
                .status(orderResponseDTO.status())
                .items(orderResponseDTO.items())
                .createdAt(orderResponseDTO.createdAt())
                .build();
    }
}
