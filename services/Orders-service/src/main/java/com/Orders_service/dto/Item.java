package com.Orders_service.dto;

import java.util.UUID;

public record Item(
    UUID productId,
    int quantity
) {}
