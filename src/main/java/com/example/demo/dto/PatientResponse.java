package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.demo.model.CommunicationMethod;
import com.example.demo.model.EducationLevel;
import com.example.demo.model.MaritalStatus;
import com.example.demo.model.Sex;
import com.example.demo.model.Patient;
import com.example.demo.dto.NextOfKinRequest;

public record PatientResponse(
        UUID id,
        String patientNumber,
        String fullName,
        String previousName,
        LocalDate dateOfBirth,
        Integer age,
        Sex sex,
        String nationality,
        String stateOfOrigin,
        MaritalStatus maritalStatus,
        String occupation,
        EducationLevel educationLevel,
        String residentialAddress,
        String phoneNumber,
        String emailAddress,
        String preferredLanguage,
        CommunicationMethod preferredCommunicationMethod,
        List<NextOfKinResponse> nextOfKin
) {
    public static PatientResponse from(Patient p) {
        return new PatientResponse(p.getId(), p.getPatientNumber(), p.getFullName(), p.getPreviousName(),
                p.getDateOfBirth(), p.getAge(), p.getSex(), p.getNationality(), p.getStateOfOrigin(),
                p.getMaritalStatus(), p.getOccupation(), p.getEducationLevel(), p.getResidentialAddress(),
                p.getPhoneNumber(), p.getEmailAddress(), p.getPreferredLanguage(),
                p.getPreferredCommunicationMethod(),
                p.getNextOfKin().stream().map(NextOfKinResponse::from).toList());
    }
}