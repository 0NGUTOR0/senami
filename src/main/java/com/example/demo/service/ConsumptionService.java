package com.example.demo.service;

import com.example.demo.dto.ConsumptionRequest;
import com.example.demo.dto.ConsumptionResponse;
import com.example.demo.model.Consumption;
import com.example.demo.model.Incident;
import com.example.demo.model.Patient;
import com.example.demo.repository.ConsumptionRepository;
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
public class ConsumptionService {

    private static final LocalDateTime EARLIEST = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final LocalDateTime LATEST = LocalDateTime.of(9999, 12, 31, 23, 59);

    private final ConsumptionRepository consumptionRepository;
    private final PatientRepository patientRepository;
    private final IncidentRepository incidentRepository;
    private final CurrentUserService currentUserService;

    public ConsumptionService(ConsumptionRepository consumptionRepository,
                              PatientRepository patientRepository,
                              IncidentRepository incidentRepository,
                              CurrentUserService currentUserService) {
        this.consumptionRepository = consumptionRepository;
        this.patientRepository = patientRepository;
        this.incidentRepository = incidentRepository;
        this.currentUserService = currentUserService;
    }

    public ConsumptionResponse record(UUID patientId, ConsumptionRequest r) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + patientId));

        Incident incident = null;
        if (r.incidentId() != null) {
            incident = incidentRepository.findById(r.incidentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Incident not found: " + r.incidentId()));
            if (!incident.getPatient().getId().equals(patientId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incident belongs to a different patient");
            }
        }

        Consumption c = new Consumption();
        c.setPatient(patient);
        c.setIncident(incident);
        c.setRecordedBy(currentUserService.getCurrentUser());
        c.setCategory(r.category());
        c.setItemName(r.itemName());
        c.setQuantity(r.quantity());
        c.setUnit(r.unit());
        c.setConsumedAt(r.consumedAt());
        c.setNotes(r.notes());
        return ConsumptionResponse.from(consumptionRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<ConsumptionResponse> listForPatient(UUID patientId, LocalDateTime from,
                                                    LocalDateTime to, Pageable pageable) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found: " + patientId);
        }
        return consumptionRepository
                .findByPatientIdAndConsumedAtBetween(patientId,
                        from == null ? EARLIEST : from,
                        to == null ? LATEST : to,
                        pageable)
                .map(ConsumptionResponse::from);
    }

    @Transactional(readOnly = true)
    public ConsumptionResponse getById(UUID id) {
        return consumptionRepository.findWithDetailsById(id)
                .map(ConsumptionResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Consumption record not found: " + id));
    }
}
