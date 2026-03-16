package com.Orders_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreationRequestDTO(
        @NotBlank String customerId,
        @NotEmpty List<Item> items
) {}