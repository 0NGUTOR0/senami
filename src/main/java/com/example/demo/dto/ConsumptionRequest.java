package com.example.demo.dto;

import com.example.demo.model.ConsumptionCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsumptionRequest(
        @NotNull ConsumptionCategory category,
        @NotBlank @Size(max = 150) String itemName,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal quantity,
        @NotBlank @Size(max = 30) String unit,
        @NotNull @PastOrPresent LocalDateTime consumedAt,
        @Size(max = 500) String notes,
        UUID incidentId
) {
}
