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

    /** Name, previous name, patient number or phone number (case-insensitive, partial). An empty term matches everyone. */
    String MATCH = "(lower(p.fullName) like lower(concat('%', :term, '%'))"
            + " or lower(p.previousName) like lower(concat('%', :term, '%'))"
            + " or lower(p.patientNumber) like lower(concat('%', :term, '%'))"
            + " or p.phoneNumber like concat('%', :term, '%'))";

    @EntityGraph(attributePaths = "caseWorker")
    Optional<Patient> findByPatientNumber(String patientNumber);

    List<Patient> findAll();

    boolean existsByPatientNumber(String patientNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAddressIgnoreCase(String emailAddress);

    /** Loads the patient together with next of kin and case worker. */
    @EntityGraph(attributePaths = {"nextOfKin", "caseWorker"})
    Optional<Patient> findWithNextOfKinById(UUID id);

    /** Every patient that matches. For admins. */
    @EntityGraph(attributePaths = "caseWorker")
    @Query(value = "select p from Patient p where " + MATCH,
           countQuery = "select count(p) from Patient p where " + MATCH)
    Page<Patient> search(@Param("term") String term, Pageable pageable);

    /** Only the patients of one case worker. Used for staff, and for the admin filter. */
    @EntityGraph(attributePaths = "caseWorker")
    @Query(value = "select p from Patient p where p.caseWorker.id = :caseWorkerId and " + MATCH,
           countQuery = "select count(p) from Patient p where p.caseWorker.id = :caseWorkerId and " + MATCH)
    Page<Patient> searchByCaseWorker(@Param("term") String term,
                                     @Param("caseWorkerId") Long caseWorkerId,
                                     Pageable pageable);

    /** Patients nobody is assigned to. Admin filter only. */
    @EntityGraph(attributePaths = "caseWorker")
    @Query(value = "select p from Patient p where p.caseWorker is null and " + MATCH,
           countQuery = "select count(p) from Patient p where p.caseWorker is null and " + MATCH)
    Page<Patient> searchUnassigned(@Param("term") String term, Pageable pageable);
}
