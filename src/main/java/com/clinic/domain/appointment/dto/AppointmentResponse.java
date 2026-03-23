package com.clinic.domain.appointment.dto;

import com.clinic.domain.appointment.AppointmentStatus;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class AppointmentResponse {
    Long id;
    Long patientId;
    String patientName;
    Long doctorId;
    String doctorName;
    LocalDateTime appointmentDatetime;
    String reason;
    AppointmentStatus status;
}