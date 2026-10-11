package com.example.demo.service;

import com.example.demo.dto.IncidentRequest;
import com.example.demo.dto.IncidentResponse;
import com.example.demo.dto.IncidentReviewRequest;
import com.example.demo.model.Incident;
import com.example.demo.model.Patient;
import com.example.demo.repository.IncidentRepository;
import com.example.demo.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    public IncidentService(IncidentRepository incidentRepository,
                           PatientRepository patientRepository,
                           CurrentUserService currentUserService) {
        this.incidentRepository = incidentRepository;
        this.patientRepository = patientRepository;
        this.currentUserService = currentUserService;
    }

    /** Staff completes the form for a patient. The staff member is the logged-in user. */
    public IncidentResponse record(UUID patientId, IncidentRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + patientId));
        Incident incident = new Incident();
        incident.setPatient(patient);
        incident.setRecordedBy(currentUserService.getCurrentUser());
        applyForm(incident, request);
        return IncidentResponse.from(incidentRepository.save(incident));
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> listForPatient(UUID patientId, Pageable pageable) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found: " + patientId);
        }
        return incidentRepository.findByPatientId(patientId, pageable).map(IncidentResponse::from);
    }

    @Transactional(readOnly = true)
    public IncidentResponse getById(UUID id) {
        return IncidentResponse.from(find(id));
    }

    /** Corrects the staff part of the form; the change is logged against the logged-in user. */
    public IncidentResponse update(UUID id, IncidentRequest request) {
        Incident incident = find(id);
        incident.setLastModifiedBy(currentUserService.getCurrentUser());
        incident.setLastModifiedAt(LocalDateTime.now());
        applyForm(incident, request);
        return IncidentResponse.from(incident);
    }

    /** The person receiving the report completes the review. Their name and the time are recorded automatically. */
    public IncidentResponse review(UUID id, IncidentReviewRequest r) {
        Incident i = find(id);
        i.setReceivedBy(currentUserService.getCurrentUser());
        i.setReceivedAt(LocalDateTime.now());
        i.setFollowUpAgencies(r.followUpAgencies());
        i.setClassifiedWhs(r.classifiedWhs());
        i.setClassifiedBehaviour(r.classifiedBehaviour());
        i.setClassifiedStaffWellbeing(r.classifiedStaffWellbeing());
        i.setClassifiedCareConcern(r.classifiedCareConcern());
        i.setRiskAssessmentFormCompleted(r.riskAssessmentFormCompleted());
        i.setInjuryFormCompleted(r.injuryFormCompleted());
        return IncidentResponse.from(i);
    }

    private Incident find(UUID id) {
        return incidentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found: " + id));
    }

    private void applyForm(Incident i, IncidentRequest r) {
        i.setOccurredAt(r.occurredAt());
        i.setLocation(r.location());
        i.setDescription(r.description());
        i.setAntecedents(r.antecedents());
        i.setInternalFactors(r.internalFactors());
        i.setBehaviours(r.behaviours());
        i.setHarmfulActivities(r.harmfulActivities());
        i.setConsequences(r.consequences());
        i.setPeopleInvolved(r.peopleInvolved());
        i.setDuration(r.duration());
        i.setFrequency(r.frequency());
        i.setSeverity(r.severity());
        i.setOnCallInvolved(r.onCallInvolved());
        i.setEmergencyServices(r.emergencyServices());
        i.setNdisReported(r.ndisReported());
        i.setStrategies(r.strategies());
        i.setCommunicationSupports(r.communicationSupports());
        i.setStrategiesEffective(r.strategiesEffective());
        i.setResponseDescription(r.responseDescription());
    }
}
