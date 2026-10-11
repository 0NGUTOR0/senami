package com.example.demo.repository;

import com.example.demo.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    @EntityGraph(attributePaths = {"patient", "recordedBy", "lastModifiedBy", "receivedBy"})
    Page<Incident> findByPatientId(UUID patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "recordedBy", "lastModifiedBy", "receivedBy"})
    Optional<Incident> findWithDetailsById(UUID id);
}
