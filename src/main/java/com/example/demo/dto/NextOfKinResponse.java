package com.example.demo.dto;

import java.util.UUID;
import com.example.demo.model.NextOfKin;
import com.example.demo.model.Patient;

public record NextOfKinResponse(
        UUID id,
        String fullName,
        String relationshipToPatient,
        String phoneNumber,
        String alternativePhoneNumber,
        String address,
        String email,
        boolean authorizedToReceiveInformation
) {
    static NextOfKinResponse from(NextOfKin k) {
        return new NextOfKinResponse(k.getId(), k.getFullName(), k.getRelationshipToPatient(),
                k.getPhoneNumber(), k.getAlternativePhoneNumber(), k.getAddress(), k.getEmail(),
                k.isAuthorizedToReceiveInformation());
    }
}
