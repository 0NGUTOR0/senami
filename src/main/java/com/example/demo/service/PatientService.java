package com.example.demo.service;

import com.example.demo.dto.PatientRequest;
import com.example.demo.dto.PatientResponse;
import com.example.demo.dto.NextOfKinRequest;
import com.example.demo.model.Patient;
import com.example.demo.model.NextOfKin;
import com.example.demo.model.User;
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
    private final PatientAccessService access;

    public PatientService(PatientRepository repository, PatientAccessService access) {
        this.repository = repository;
        this.access = access;
    }

    public PatientResponse create(PatientRequest request) {
        if (repository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicatePatientException("A patient with this phone number already exists");
        }
        Patient patient = new Patient();
        apply(patient, request);
        // A patient a staff member creates is theirs. An admin assigns the case worker afterwards.
        if (!access.isAdmin()) {
            patient.setCaseWorker(access.currentUser());
        }
        return PatientResponse.from(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(UUID id) {
        return PatientResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public PatientResponse getByPatientNumber(String patientNumber) {
        Patient patient = repository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + patientNumber));
        access.requireAccess(patient);
        return PatientResponse.from(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> getAll(Pageable pageable) {
        return search(null, null, false, pageable);
    }

    /**
     * Staff always get only their own patients, whatever filter they send.
     * Admins get everyone, or one case worker's patients, or the unassigned ones.
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String term, Long caseWorkerId, boolean unassigned, Pageable pageable) {
        String t = term == null ? "" : term.trim();
        Page<Patient> page;
        if (!access.isAdmin()) {
            page = repository.searchByCaseWorker(t, access.currentUser().getId(), pageable);
        } else if (unassigned) {
            page = repository.searchUnassigned(t, pageable);
        } else if (caseWorkerId != null) {
            page = repository.searchByCaseWorker(t, caseWorkerId, pageable);
        } else {
            page = repository.search(t, pageable);
        }
        return page.map(PatientResponse::from);
    }

    /** Admin only. A null caseWorkerId removes the assignment. */
    public PatientResponse assignCaseWorker(UUID id, Long caseWorkerId) {
        access.requireAdmin();
        Patient patient = find(id);
        User caseWorker = caseWorkerId == null ? null : access.findUser(caseWorkerId);
        patient.setCaseWorker(caseWorker);
        return PatientResponse.from(patient);
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
        Patient patient = repository.findWithNextOfKinById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + id));
        access.requireAccess(patient);
        return patient;
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
