package com.Orders_service.service;

import com.Orders_service.dto.EventDTO;
import com.Orders_service.dto.Item;
import com.Orders_service.dto.OrderCreationRequestDTO;
import com.Orders_service.dto.OrderResponseDTO;
import com.Orders_service.model.Order;
import com.Orders_service.model.OutboxEvent;
import com.Orders_service.repository.OrderRepository;
import com.Orders_service.repository.OutboxRepository;
import com.Orders_service.repository.ProductRepository;
import com.Orders_service.util.Mapper;
import jakarta.transaction.Transactional;
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
    private final Mapper mapper;
    private final ProductRepository productRepository;
    private final OutboxRepository outboxRepository;

    /**
     * Creates a new order based on the provided request data, saves it to the database, and
     * persists corresponding domain events to the event outbox table.
     * <p>
     * The method will:
     * \- check stock for all requested items,
     * \- always create and save the order,
     * \- set the status to "CREATED" if all items are in stock, otherwise "PENDING_RESTOCK",
     * \- always write an "order_created" event to the event outbox,
     * \- if any items are out of stock, also write a "restock_request" event to the event outbox.
     *
     * @param request the order creation request data containing customerId and list of items
     * @return an OrderResponseDTO representing the created order
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderCreationRequestDTO request) {
        log.info("Creating order for customer {} for items {}", request.customerId(), request.items());

        // 1. Check stock for all items first
        List<Item> items = request.items();
        List<Item> outOfStockItems = items.stream()
                .filter(item -> !productRepository.existsById(item.productId()))
                .toList();

        boolean allInStock = outOfStockItems.isEmpty();

        JsonNode itemsNode = objectMapper.valueToTree(request.items());

        // 2. Set order status based on stock availability
        String status = allInStock ? "CREATED" : "PENDING_RESTOCK";

        Order order = Order.builder()
                .customerId(request.customerId())
                .items(itemsNode)
                .status(status)
                .createdAt(Instant.now())
                .build();

        // 3. Persist order
        Order savedOrder = orderRepository.save(order);

        // 4. Map to response DTO
        OrderResponseDTO responseDTO = mapper.mapToResponse(savedOrder);

        // 5. Write ORDER_CREATED_EVENT to outbox
        OutboxEvent orderCreatedOutbox = OutboxEvent.builder()
                .eventType(ORDER_CREATED_EVENT)
                .customerId(responseDTO.customerId())
                .orderId(responseDTO.id().toString())
                .status(NEW)
                .items(objectMapper.valueToTree(responseDTO.items()))
                .createdAt(Instant.now())
                .build();
        outboxRepository.save(orderCreatedOutbox);

        // 6. If some items are out of stock, write RESTOCK_REQUEST_EVENT to outbox
        if (!allInStock) {
            log.warn("Some products are out of stock for order {}. Creating restock outbox event.", savedOrder.getId());

            OutboxEvent restockOutbox = OutboxEvent.builder()
                    .eventType(RESTOCK_REQUEST_EVENT)
                    .customerId(responseDTO.customerId())
                    .orderId(responseDTO.id().toString())
                    .status(NEW)
                    .items(objectMapper.valueToTree(responseDTO.items()))
                    .createdAt(Instant.now())
                    .build();
            outboxRepository.save(restockOutbox);
        }

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
