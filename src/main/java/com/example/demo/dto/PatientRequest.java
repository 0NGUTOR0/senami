package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.example.demo.model.CommunicationMethod;
import com.example.demo.model.EducationLevel;
import com.example.demo.model.MaritalStatus;
import com.example.demo.model.Sex;
import com.example.demo.dto.NextOfKinRequest;
import java.time.LocalDate;
import java.util.List;

/** Payload for creating or updating a patient. Record ID and age are system-managed. */
public record PatientRequest(
        @NotBlank @Size(max = 150) String fullName,
        @Size(max = 150) String previousName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull Sex sex,
        @Size(max = 80) String nationality,
        @Size(max = 80) String stateOfOrigin,
        MaritalStatus maritalStatus,
        @Size(max = 100) String occupation,
        EducationLevel educationLevel,
        @Size(max = 300) String residentialAddress,
        @NotBlank @Pattern(regexp = "^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number") String phoneNumber,
        @Email @Size(max = 150) String emailAddress,
        @Size(max = 50) String preferredLanguage,
        CommunicationMethod preferredCommunicationMethod,
        @Valid List<NextOfKinRequest> nextOfKin
) {
}