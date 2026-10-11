package com.example.demo.dto;

import com.example.demo.model.Incident;
import com.example.demo.model.IncidentOptions;
import com.example.demo.model.Patient;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        UUID patientId,
        String patientNumber,
        String clientName,
        String clientAddress,
        String staffName,
        String staffEmail,
        LocalDateTime recordedAt,
        LocalDateTime occurredAt,
        IncidentOptions.Location location,
        String description,
        Set<IncidentOptions.Antecedent> antecedents,
        Set<IncidentOptions.InternalFactor> internalFactors,
        Set<IncidentOptions.Behaviour> behaviours,
        Set<IncidentOptions.HarmfulActivity> harmfulActivities,
        Set<IncidentOptions.Consequence> consequences,
        Set<IncidentOptions.PersonInvolved> peopleInvolved,
        IncidentOptions.Duration duration,
        IncidentOptions.Frequency frequency,
        IncidentOptions.Severity severity,
        Boolean onCallInvolved,
        Set<IncidentOptions.EmergencyService> emergencyServices,
        Boolean ndisReported,
        Set<IncidentOptions.Strategy> strategies,
        Set<IncidentOptions.CommunicationSupport> communicationSupports,
        Boolean strategiesEffective,
        String responseDescription,
        String lastModifiedByName,
        LocalDateTime lastModifiedAt,
        boolean reviewed,
        String receivedByName,
        LocalDateTime receivedAt,
        Set<IncidentOptions.FollowUpAgency> followUpAgencies,
        Boolean classifiedWhs,
        Boolean classifiedBehaviour,
        Boolean classifiedStaffWellbeing,
        Boolean classifiedCareConcern,
        Boolean riskAssessmentFormCompleted,
        Boolean injuryFormCompleted
) {
    public static IncidentResponse from(Incident i) {
        Patient p = i.getPatient();
        return new IncidentResponse(
                i.getId(),
                p.getId(),
                p.getPatientNumber(),
                p.getFullName(),
                p.getResidentialAddress(),
                i.getRecordedBy().getUsername(),
                i.getRecordedBy().getEmail(),
                i.getRecordedAt(),
                i.getOccurredAt(),
                i.getLocation(),
                i.getDescription(),
                sorted(i.getAntecedents()),
                sorted(i.getInternalFactors()),
                sorted(i.getBehaviours()),
                sorted(i.getHarmfulActivities()),
                sorted(i.getConsequences()),
                sorted(i.getPeopleInvolved()),
                i.getDuration(),
                i.getFrequency(),
                i.getSeverity(),
                i.getOnCallInvolved(),
                sorted(i.getEmergencyServices()),
                i.getNdisReported(),
                sorted(i.getStrategies()),
                sorted(i.getCommunicationSupports()),
                i.getStrategiesEffective(),
                i.getResponseDescription(),
                i.getLastModifiedBy() == null ? null : i.getLastModifiedBy().getUsername(),
                i.getLastModifiedAt(),
                i.getReceivedAt() != null,
                i.getReceivedBy() == null ? null : i.getReceivedBy().getUsername(),
                i.getReceivedAt(),
                sorted(i.getFollowUpAgencies()),
                i.getClassifiedWhs(),
                i.getClassifiedBehaviour(),
                i.getClassifiedStaffWellbeing(),
                i.getClassifiedCareConcern(),
                i.getRiskAssessmentFormCompleted(),
                i.getInjuryFormCompleted());
    }

    /** Selected options in form order rather than database order. */
    private static <E extends Enum<E>> Set<E> sorted(Set<E> values) {
        return new TreeSet<>(values);
    }
}
