package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

@Entity
@Table(name = "next_of_kin")
public class NextOfKin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank
    @Size(max = 150)
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "relationship_to_patient", nullable = false, length = 50)
    private String relationshipToPatient;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number")
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Pattern(regexp = "^$|^\\+?[0-9 ()-]{7,20}$", message = "Invalid phone number")
    @Column(name = "alternative_phone_number", length = 20)
    private String alternativePhoneNumber;

    @Size(max = 300)
    @Column(length = 300)
    private String address;

    @Email
    @Size(max = 150)
    @Column(length = 150)
    private String email;

    /** Whether this person may receive information about the patient. */
    @Column(name = "authorized_to_receive_information", nullable = false)
    private boolean authorizedToReceiveInformation;

    public UUID getId() { return id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRelationshipToPatient() { return relationshipToPatient; }
    public void setRelationshipToPatient(String r) { this.relationshipToPatient = r; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
    public void setAlternativePhoneNumber(String n) { this.alternativePhoneNumber = n; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAuthorizedToReceiveInformation() { return authorizedToReceiveInformation; }
    public void setAuthorizedToReceiveInformation(boolean v) { this.authorizedToReceiveInformation = v; }
}
