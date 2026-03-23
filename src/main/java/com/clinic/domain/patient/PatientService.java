package com.clinic.domain.patient;

import com.clinic.domain.doctor.Doctor;
import com.clinic.domain.doctor.DoctorRepository;
import com.clinic.domain.patient.dto.CreatePatientRequest;
import com.clinic.domain.patient.dto.PatientResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + id
                ));
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "A patient with this email already exists"
            );
        }

        Patient patient = new Patient();
        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());

        if (request.getPreferredDoctorId() != null) {
            Doctor doctor = doctorRepository
                    .findById(request.getPreferredDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Doctor not found with id: " + request.getPreferredDoctorId()
                    ));
            patient.setPreferredDoctor(doctor);
        }

        Patient saved = patientRepository.save(patient);
        return toResponse(saved);
    }

    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getPreferredDoctor() != null
                        ? patient.getPreferredDoctor().getId()
                        : null
        );
    }
}