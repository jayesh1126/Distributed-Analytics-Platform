package com.Orders_service.service;

import com.Orders_service.dto.EventDTO;
import com.Orders_service.dto.Item;
import com.Orders_service.dto.OrderCreationRequestDTO;
import com.Orders_service.dto.OrderResponseDTO;
import com.Orders_service.model.Order;
import com.Orders_service.repository.OrderRepository;
import com.Orders_service.repository.ProductRepository;
import com.Orders_service.util.Mapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.Instant;
import java.util.List;

import static com.Orders_service.util.Constants.*;

/**
 * Service responsible for handling order-related operations such as creating and retrieving orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final EventProducer eventProducer;
    private final Mapper mapper;
    private final ProductRepository productRepository;

    /**
     * Creates a new order based on the provided request data, saves it to the database, and produces an appropriate event to a Kafka topic.
     *
     * @param request The order creation request data containing customerId, and list of items.
     * @return An OrderCreationResponseDTO representing the created order.
     */
    public OrderResponseDTO createOrder(OrderCreationRequestDTO request) {
        log.info("Creating order for customer {} for items {}", request.customerId(), request.items());
        JsonNode itemsNode = objectMapper.valueToTree(request.items());

        Order order = Order.builder()
                .customerId(request.customerId())
                .items(itemsNode)
                .createdAt(Instant.now())
                .build();

        Order saved = orderRepository.save(order);
        OrderResponseDTO responseDTO = mapper.mapToResponse(saved);
        eventProducer.sendEvent(ORDERS_TOPIC, mapper.mapToEventWithType(responseDTO, ORDER_CREATED_EVENT));

//        Check Product table to see if the items in the order are in stock.
        responseDTO.items()
                .forEach(item -> {
                    if (!productRepository.existsById(item.productId())) {
                        log.warn("Product with id {} is out of stock. Sending restock notification.", item.productId());
                        EventDTO restockEvent = EventDTO.builder()
                                .eventType(RESTOCK_REQUEST_EVENT)
                                .customerId(responseDTO.customerId())
                                .orderId(responseDTO.id().toString())
                                .status(responseDTO.status())
                                .items(List.of(item))
                                .createdAt(responseDTO.createdAt())
                                .build();
                        eventProducer.sendEvent(STOCK_TOPIC, restockEvent);
                    }
                });
        return responseDTO;
    }

    /**
     * Retrieves all orders from the database and maps them to a list of OrderResponseDTOs.
     *
     * @return A list of OrderResponseDTOs representing all events in the database.
     */
    public List<OrderResponseDTO> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }
}
