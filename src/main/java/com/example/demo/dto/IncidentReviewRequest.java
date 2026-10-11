package com.example.demo.dto;

import com.example.demo.model.IncidentOptions;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/** The review section, completed by the person receiving the report. Who and when are filled in automatically. */
public record IncidentReviewRequest(
        Set<IncidentOptions.FollowUpAgency> followUpAgencies,
        @NotNull Boolean classifiedWhs,
        @NotNull Boolean classifiedBehaviour,
        @NotNull Boolean classifiedStaffWellbeing,
        @NotNull Boolean classifiedCareConcern,
        @NotNull Boolean riskAssessmentFormCompleted,
        @NotNull Boolean injuryFormCompleted
) {
}
