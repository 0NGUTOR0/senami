package com.example.demo.service;

import com.example.demo.dto.PatientRequest;
import com.example.demo.dto.PatientResponse;
import com.example.demo.dto.NextOfKinRequest;
import com.example.demo.model.Patient;
import com.example.demo.model.NextOfKin;
import com.example.demo.repository.PatientRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public PatientResponse create(PatientRequest request) {
        if (repository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicatePatientException("A patient with this phone number already exists");
        }
        Patient patient = new Patient();
        apply(patient, request);
        return PatientResponse.from(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(UUID id) {
        return PatientResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public PatientResponse getByPatientNumber(String patientNumber) {
        return repository.findByPatientNumber(patientNumber)
                .map(PatientResponse::from)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + patientNumber));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(PatientResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String term, Pageable pageable) {
        Page<Patient> page = (term == null || term.isBlank())
                ? repository.findAll(pageable)
                : repository.search(term.trim(), pageable);
        return page.map(PatientResponse::from);
    }

    public PatientResponse update(UUID id, PatientRequest request) {
        Patient patient = find(id);
        if (!patient.getPhoneNumber().equals(request.phoneNumber())
                && repository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicatePatientException("A patient with this phone number already exists");
        }
        apply(patient, request);
        return PatientResponse.from(patient);
    }

    public void delete(UUID id) {
        repository.delete(find(id));
    }

    private Patient find(UUID id) {
        return repository.findWithNextOfKinById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + id));
    }

    private void apply(Patient p, PatientRequest r) {
        p.setFullName(r.fullName());
        p.setPreviousName(r.previousName());
        p.setDateOfBirth(r.dateOfBirth());
        p.setSex(r.sex());
        p.setNationality(r.nationality());
        p.setStateOfOrigin(r.stateOfOrigin());
        p.setMaritalStatus(r.maritalStatus());
        p.setOccupation(r.occupation());
        p.setEducationLevel(r.educationLevel());
        p.setResidentialAddress(r.residentialAddress());
        p.setPhoneNumber(r.phoneNumber());
        p.setEmailAddress(r.emailAddress());
        p.setPreferredLanguage(r.preferredLanguage());
        p.setPreferredCommunicationMethod(r.preferredCommunicationMethod());

        // Replace next of kin; orphanRemoval deletes the ones no longer present.
        p.getNextOfKin().clear();
        List<NextOfKinRequest> kin = r.nextOfKin() == null ? List.of() : r.nextOfKin();
        for (NextOfKinRequest k : kin) {
            NextOfKin entity = new NextOfKin();
            entity.setFullName(k.fullName());
            entity.setRelationshipToPatient(k.relationshipToPatient());
            entity.setPhoneNumber(k.phoneNumber());
            entity.setAlternativePhoneNumber(k.alternativePhoneNumber());
            entity.setAddress(k.address());
            entity.setEmail(k.email());
            entity.setAuthorizedToReceiveInformation(k.authorizedToReceiveInformation());
            p.addNextOfKin(entity);
        }
    }
}
