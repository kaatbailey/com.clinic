package com.clinic.domain.patient.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class PatientResponse {
    Long id;
    String fullName;
    LocalDate dateOfBirth;
    String email;
    String phoneNumber;
    Long preferredDoctorId;
}