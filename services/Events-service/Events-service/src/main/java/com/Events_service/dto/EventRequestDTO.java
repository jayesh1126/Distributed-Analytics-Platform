package com.Events_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record EventRequestDTO(
        @NotBlank String type,
        @NotBlank String userId,
        Map<String, Object> payload
) {}