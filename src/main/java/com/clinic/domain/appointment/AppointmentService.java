package com.clinic.domain.appointment;

import com.clinic.domain.appointment.dto.AppointmentResponse;
import com.clinic.domain.appointment.dto.CreateAppointmentRequest;
import com.clinic.domain.doctor.Doctor;
import com.clinic.domain.doctor.DoctorAvailability;
import com.clinic.domain.doctor.DoctorAvailabilityRepository;
import com.clinic.domain.doctor.DoctorRepository;
import com.clinic.domain.patient.Patient;
import com.clinic.domain.patient.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment not found with id: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {

        // Verify patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + request.getPatientId()
                ));

        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()
                ));

        // Check doctor works on this day
        DayOfWeek requestedDay = request.getAppointmentDatetime().getDayOfWeek();
        List<DoctorAvailability> availability = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeek(request.getDoctorId(), requestedDay);

        if (availability.isEmpty()) {
            throw new IllegalArgumentException(
                    doctor.getFullName() + " does not work on " + requestedDay
            );
        }

        // Check requested time falls within availability window
        boolean withinAvailableHours = availability.stream()
                .anyMatch(slot ->
                        !request.getAppointmentDatetime().toLocalTime().isBefore(slot.getStartTime()) &&
                                !request.getAppointmentDatetime().toLocalTime().isAfter(slot.getEndTime())
                );

        if (!withinAvailableHours) {
            throw new IllegalArgumentException(
                    doctor.getFullName() + " is not available at the requested time"
            );
        }

        // Check doctor is not already booked at this time
        boolean alreadyBooked = appointmentRepository
                .existsByDoctorIdAndAppointmentDatetime(
                        request.getDoctorId(),
                        request.getAppointmentDatetime()
                );

        if (alreadyBooked) {
            throw new IllegalArgumentException(
                    doctor.getFullName() + " already has an appointment at this time"
            );
        }

        // All checks passed — create the appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDatetime(request.getAppointmentDatetime());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id, boolean cancelledByDoctor) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment not found with id: " + id
                ));

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new IllegalStateException(
                    "Only booked appointments can be cancelled"
            );
        }

        appointment.setStatus(cancelledByDoctor
                ? AppointmentStatus.DOCTOR_CANCELLED
                : AppointmentStatus.PATIENT_CANCELLED
        );

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFullName(),
                appointment.getAppointmentDatetime(),
                appointment.getReason(),
                appointment.getStatus()
        );
    }
}
