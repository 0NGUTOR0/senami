package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NextOfKinRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Size(max = 50) String relationshipToPatient,
        @NotBlank @Pattern(regexp = "^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number") String phoneNumber,
        @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number") String alternativePhoneNumber,
        @Size(max = 300) String address,
        @Email @Size(max = 150) String email,
        boolean authorizedToReceiveInformation
) {
}