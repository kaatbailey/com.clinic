package com.clinic.domain.doctor.dto;

import lombok.Value;

@Value
public class DoctorResponse {
    Long id;
    String fullName;
    String email;
    String phoneNumber;
    String specialty;
    String licenseNumber;
}