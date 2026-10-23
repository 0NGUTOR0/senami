package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An incident report, following the SK Incident Report form.
 * Part 1 is completed by the staff member (the logged-in user); part 2 is the review by the person receiving the report.
 */
@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_incident_patient", columnList = "patient_id"),
        @Index(name = "idx_incident_occurred_at", columnList = "occurred_at"),
        @Index(name = "idx_incident_import_batch", columnList = "import_batch_id"),
        @Index(name = "idx_incident_external_id", columnList = "external_id")
})
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Client name and address come from the patient record. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, updatable = false)
    private Patient patient;

    /** Staff name: the logged-in user who recorded the incident. Set once, never changed. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by_id", nullable = false, updatable = false)
    private User recordedBy;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by_id")
    private User lastModifiedBy;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // ---- Part 1: completed by the staff member ----

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false, length = 60)
    private IncidentOptions.Location location;

    @Column(name = "description", nullable = false, length = 5000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "incident_antecedents", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.Antecedent> antecedents = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_internal_factors", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.InternalFactor> internalFactors = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_behaviours", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.Behaviour> behaviours = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_harmful_activities", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.HarmfulActivity> harmfulActivities = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_consequences", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.Consequence> consequences = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_people_involved", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.PersonInvolved> peopleInvolved = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false, length = 60)
    private IncidentOptions.Duration duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 60)
    private IncidentOptions.Frequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 60)
    private IncidentOptions.Severity severity;

    @Column(name = "on_call_involved", nullable = false)
    private Boolean onCallInvolved;

    @ElementCollection
    @CollectionTable(name = "incident_emergency_services", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.EmergencyService> emergencyServices = new HashSet<>();

    @Column(name = "ndis_reported", nullable = false)
    private Boolean ndisReported;

    @ElementCollection
    @CollectionTable(name = "incident_strategies", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.Strategy> strategies = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "incident_communication_supports", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.CommunicationSupport> communicationSupports = new HashSet<>();

    @Column(name = "strategies_effective", nullable = false)
    private Boolean strategiesEffective;

    @Column(name = "response_description", nullable = true, length = 3000)
    private String responseDescription;

    // ---- Part 2: completed by the person receiving the report ----

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_id")
    private User receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @ElementCollection
    @CollectionTable(name = "incident_follow_up_agencies", joinColumns = @JoinColumn(name = "incident_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "option_value", length = 120)
    @BatchSize(size = 50)
    private Set<IncidentOptions.FollowUpAgency> followUpAgencies = new HashSet<>();

    @Column(name = "classified_whs", nullable = true)
    private Boolean classifiedWhs;

    @Column(name = "classified_behaviour", nullable = true)
    private Boolean classifiedBehaviour;

    @Column(name = "classified_staff_wellbeing", nullable = true)
    private Boolean classifiedStaffWellbeing;

    @Column(name = "classified_care_concern", nullable = true)
    private Boolean classifiedCareConcern;

    @Column(name = "risk_assessment_form_completed", nullable = true)
    private Boolean riskAssessmentFormCompleted;

    @Column(name = "injury_form_completed", nullable = true)
    private Boolean injuryFormCompleted;

    // ---- Where the record came from (null = entered on the form) ----

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "import_batch_id")
    private UUID importBatchId;

    @PrePersist
    void onCreate() {
        recordedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public UUID getImportBatchId() { return importBatchId; }
    public void setImportBatchId(UUID importBatchId) { this.importBatchId = importBatchId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getRecordedAt() { return recordedAt; }

    public User getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(User u) { this.lastModifiedBy = u; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime t) { this.lastModifiedAt = t; }

    public User getReceivedBy() { return receivedBy; }
    public void setReceivedBy(User u) { this.receivedBy = u; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime t) { this.receivedAt = t; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    public IncidentOptions.Location getLocation() { return location; }
    public void setLocation(IncidentOptions.Location location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<IncidentOptions.Antecedent> getAntecedents() { return antecedents; }
    public void setAntecedents(Set<IncidentOptions.Antecedent> values) {
        this.antecedents.clear();
        if (values != null) {
            this.antecedents.addAll(values);
        }
    }

    public Set<IncidentOptions.InternalFactor> getInternalFactors() { return internalFactors; }
    public void setInternalFactors(Set<IncidentOptions.InternalFactor> values) {
        this.internalFactors.clear();
        if (values != null) {
            this.internalFactors.addAll(values);
        }
    }

    public Set<IncidentOptions.Behaviour> getBehaviours() { return behaviours; }
    public void setBehaviours(Set<IncidentOptions.Behaviour> values) {
        this.behaviours.clear();
        if (values != null) {
            this.behaviours.addAll(values);
        }
    }

    public Set<IncidentOptions.HarmfulActivity> getHarmfulActivities() { return harmfulActivities; }
    public void setHarmfulActivities(Set<IncidentOptions.HarmfulActivity> values) {
        this.harmfulActivities.clear();
        if (values != null) {
            this.harmfulActivities.addAll(values);
        }
    }

    public Set<IncidentOptions.Consequence> getConsequences() { return consequences; }
    public void setConsequences(Set<IncidentOptions.Consequence> values) {
        this.consequences.clear();
        if (values != null) {
            this.consequences.addAll(values);
        }
    }

    public Set<IncidentOptions.PersonInvolved> getPeopleInvolved() { return peopleInvolved; }
    public void setPeopleInvolved(Set<IncidentOptions.PersonInvolved> values) {
        this.peopleInvolved.clear();
        if (values != null) {
            this.peopleInvolved.addAll(values);
        }
    }

    public IncidentOptions.Duration getDuration() { return duration; }
    public void setDuration(IncidentOptions.Duration duration) { this.duration = duration; }

    public IncidentOptions.Frequency getFrequency() { return frequency; }
    public void setFrequency(IncidentOptions.Frequency frequency) { this.frequency = frequency; }

    public IncidentOptions.Severity getSeverity() { return severity; }
    public void setSeverity(IncidentOptions.Severity severity) { this.severity = severity; }

    public Boolean getOnCallInvolved() { return onCallInvolved; }
    public void setOnCallInvolved(Boolean onCallInvolved) { this.onCallInvolved = onCallInvolved; }

    public Set<IncidentOptions.EmergencyService> getEmergencyServices() { return emergencyServices; }
    public void setEmergencyServices(Set<IncidentOptions.EmergencyService> values) {
        this.emergencyServices.clear();
        if (values != null) {
            this.emergencyServices.addAll(values);
        }
    }

    public Boolean getNdisReported() { return ndisReported; }
    public void setNdisReported(Boolean ndisReported) { this.ndisReported = ndisReported; }

    public Set<IncidentOptions.Strategy> getStrategies() { return strategies; }
    public void setStrategies(Set<IncidentOptions.Strategy> values) {
        this.strategies.clear();
        if (values != null) {
            this.strategies.addAll(values);
        }
    }

    public Set<IncidentOptions.CommunicationSupport> getCommunicationSupports() { return communicationSupports; }
    public void setCommunicationSupports(Set<IncidentOptions.CommunicationSupport> values) {
        this.communicationSupports.clear();
        if (values != null) {
            this.communicationSupports.addAll(values);
        }
    }

    public Boolean getStrategiesEffective() { return strategiesEffective; }
    public void setStrategiesEffective(Boolean strategiesEffective) { this.strategiesEffective = strategiesEffective; }

    public String getResponseDescription() { return responseDescription; }
    public void setResponseDescription(String responseDescription) { this.responseDescription = responseDescription; }

    public Set<IncidentOptions.FollowUpAgency> getFollowUpAgencies() { return followUpAgencies; }
    public void setFollowUpAgencies(Set<IncidentOptions.FollowUpAgency> values) {
        this.followUpAgencies.clear();
        if (values != null) {
            this.followUpAgencies.addAll(values);
        }
    }

    public Boolean getClassifiedWhs() { return classifiedWhs; }
    public void setClassifiedWhs(Boolean classifiedWhs) { this.classifiedWhs = classifiedWhs; }

    public Boolean getClassifiedBehaviour() { return classifiedBehaviour; }
    public void setClassifiedBehaviour(Boolean classifiedBehaviour) { this.classifiedBehaviour = classifiedBehaviour; }

    public Boolean getClassifiedStaffWellbeing() { return classifiedStaffWellbeing; }
    public void setClassifiedStaffWellbeing(Boolean classifiedStaffWellbeing) { this.classifiedStaffWellbeing = classifiedStaffWellbeing; }

    public Boolean getClassifiedCareConcern() { return classifiedCareConcern; }
    public void setClassifiedCareConcern(Boolean classifiedCareConcern) { this.classifiedCareConcern = classifiedCareConcern; }

    public Boolean getRiskAssessmentFormCompleted() { return riskAssessmentFormCompleted; }
    public void setRiskAssessmentFormCompleted(Boolean riskAssessmentFormCompleted) { this.riskAssessmentFormCompleted = riskAssessmentFormCompleted; }

    public Boolean getInjuryFormCompleted() { return injuryFormCompleted; }
    public void setInjuryFormCompleted(Boolean injuryFormCompleted) { this.injuryFormCompleted = injuryFormCompleted; }
}