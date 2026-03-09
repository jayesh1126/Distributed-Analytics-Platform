package com.Events_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventRequestDTO {

    @NotBlank
    private String type;

    @NotBlank
    private String userId;

    private Double amount;
}