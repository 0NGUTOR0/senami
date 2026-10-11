package com.example.demo.dto;

import com.example.demo.model.Consumption;
import com.example.demo.model.ConsumptionCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsumptionResponse(
        UUID id,
        UUID patientId,
        String patientNumber,
        String patientName,
        ConsumptionCategory category,
        String itemName,
        BigDecimal quantity,
        String unit,
        LocalDateTime consumedAt,
        String notes,
        UUID incidentId,
        String recordedByName,
        String recordedByEmail,
        LocalDateTime recordedAt
) {
    public static ConsumptionResponse from(Consumption c) {
        return new ConsumptionResponse(
                c.getId(),
                c.getPatient().getId(),
                c.getPatient().getPatientNumber(),
                c.getPatient().getFullName(),
                c.getCategory(),
                c.getItemName(),
                c.getQuantity(),
                c.getUnit(),
                c.getConsumedAt(),
                c.getNotes(),
                c.getIncident() == null ? null : c.getIncident().getId(),
                c.getRecordedBy().getUsername(),
                c.getRecordedBy().getEmail(),
                c.getRecordedAt());
    }
}
