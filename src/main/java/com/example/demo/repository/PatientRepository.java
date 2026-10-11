package com.example.demo.repository;

import com.example.demo.model.Patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByPatientNumber(String patientNumber);

    List<Patient> findAll();

    boolean existsByPatientNumber(String patientNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAddressIgnoreCase(String emailAddress);

    /** Loads the patient together with next of kin in a single query. */
    @EntityGraph(attributePaths = "nextOfKin")
    Optional<Patient> findWithNextOfKinById(UUID id);

    /** Search by name, patient number or phone number (case-insensitive, partial match). */
    @Query("""
            select p from Patient p
            where lower(p.fullName) like lower(concat('%', :term, '%'))
               or lower(p.previousName) like lower(concat('%', :term, '%'))
               or lower(p.patientNumber) like lower(concat('%', :term, '%'))
               or p.phoneNumber like concat('%', :term, '%')
            """)
    Page<Patient> search(@Param("term") String term, Pageable pageable);
}
