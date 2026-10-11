package com.example.demo.repository;

import com.example.demo.model.Consumption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {

    @EntityGraph(attributePaths = {"patient", "recordedBy", "incident"})
    Page<Consumption> findByPatientIdAndConsumedAtBetween(UUID patientId, LocalDateTime from,
                                                          LocalDateTime to, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "recordedBy", "incident"})
    Optional<Consumption> findWithDetailsById(UUID id);
}
