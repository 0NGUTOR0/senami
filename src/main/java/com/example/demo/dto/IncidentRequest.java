package com.example.demo.dto;

import com.example.demo.model.IncidentOptions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * The staff part of the incident form. Only the description is free text; everything else is a choice.
 * Client comes from the URL and staff from the logged-in user, so neither is sent.
 *
 * The antecedents and behaviours sets now accept the detailed subcategory enum values from the
 * classification document. Their category-prefixed enum names match the HTML checkbox values.
 */
public record IncidentRequest(
        @NotNull @PastOrPresent LocalDateTime occurredAt,
        @NotNull IncidentOptions.Location location,
        @NotBlank @Size(max = 5000) String description,
        Set<IncidentOptions.Antecedent> antecedents,
        Set<IncidentOptions.InternalFactor> internalFactors,
        Set<IncidentOptions.Behaviour> behaviours,
        Set<IncidentOptions.HarmfulActivity> harmfulActivities,
        Set<IncidentOptions.Consequence> consequences,
        Set<IncidentOptions.PersonInvolved> peopleInvolved,
        @NotNull IncidentOptions.Duration duration,
        @NotNull IncidentOptions.Frequency frequency,
        @NotNull IncidentOptions.Severity severity,
        @NotNull Boolean onCallInvolved,
        Set<IncidentOptions.EmergencyService> emergencyServices,
        @NotNull Boolean ndisReported,
        Set<IncidentOptions.Strategy> strategies,
        Set<IncidentOptions.CommunicationSupport> communicationSupports,
        @NotNull Boolean strategiesEffective,
        @Size(max = 3000) String responseDescription
) {
}
