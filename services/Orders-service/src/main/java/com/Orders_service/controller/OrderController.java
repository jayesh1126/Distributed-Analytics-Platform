package com.Orders_service.controller;

import com.Orders_service.dto.OrderCreationRequestDTO;
import com.Orders_service.dto.OrderResponseDTO;
import com.Orders_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Orders ingestion API")
public class OrderController {

    private final OrderService eventService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderCreationRequestDTO orderCreationRequestDTO){
        return eventService.createOrder(orderCreationRequestDTO);
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public List<OrderResponseDTO> getOrders() {
        return eventService.getOrders();
    }
}
