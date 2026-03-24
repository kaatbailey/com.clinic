# Clinic Appointment System API

A RESTful API for managing appointments at a small medical clinic with three
general practitioner doctors. Built as a teaching exercise to demonstrate
domain modelling, layered architecture, database design, and API design
from scratch using modern Java and Spring Boot.

---

## Technologies

| Technology | Purpose |
|---|---|
| Java 17 (Amazon Corretto) | Core language |
| Spring Boot 4.0.3 | Application framework |
| Spring Data JPA | Database access and ORM |
| Spring Security | Security configuration |
| H2 In-Memory Database | Development database |
| Hibernate | JPA implementation and schema generation |
| Lombok | Boilerplate reduction |
| Jakarta Bean Validation | Request validation |
| SpringDoc / Swagger UI | Auto-generated API documentation |
| Maven | Build and dependency management |

---

## Architecture

The application follows a strict layered architecture organised by feature
rather than by layer type.
```
Controller → Service → Repository → Entity → Database
```

Each layer has a single responsibility and communicates only with the
layer directly below it.

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Contains all business logic and rules |
| Repository | Manages all database communication |
| Entity | Maps Java classes to database tables |
| DTO | Shapes data for API input and output |

### Package Structure
```
com.clinic
├── common
│   ├── config
│   │   ├── JpaConfig.java
│   │   └── SecurityConfig.java
│   ├── entity
│   │   └── AuditableEntity.java
│   └── exception
│       ├── ErrorResponse.java
│       └── GlobalExceptionHandler.java
└── domain
    ├── doctor
    │   ├── dto
    │   │   ├── CreateDoctorRequest.java
    │   │   └── DoctorResponse.java
    │   ├── Doctor.java
    │   ├── DoctorAvailability.java
    │   ├── DoctorAvailabilityRepository.java
    │   ├── DoctorController.java
    │   ├── DoctorRepository.java
    │   └── DoctorService.java
    ├── patient
    │   ├── dto
    │   │   ├── CreatePatientRequest.java
    │   │   └── PatientResponse.java
    │   ├── Patient.java
    │   ├── PatientController.java
    │   ├── PatientRepository.java
    │   └── PatientService.java
    └── appointment
        ├── dto
        │   ├── CreateAppointmentRequest.java
        │   └── AppointmentResponse.java
        ├── Appointment.java
        ├── AppointmentController.java
        ├── AppointmentRepository.java
        ├── AppointmentService.java
        └── AppointmentStatus.java
```

---

## Key Design Decisions

**Audit Trail** — every table inherits five audit columns from
`AuditableEntity` via the `@MappedSuperclass` pattern: `created_at`,
`created_by_id`, `updated_at`, `updated_by_id`, and `ip_address`.

**Package by Feature** — code is organised by domain feature rather than
technical layer. Everything related to doctors lives together, everything
related to patients lives together. This makes the codebase easier to
navigate and reason about as it grows.

**DTO Separation** — entities are never exposed directly through the API.
Request DTOs control what callers can send. Response DTOs control what
callers can see. This decouples the API contract from the database structure.

**Booking Logic** — the appointment service enforces three rules before
creating a booking: the doctor must work on the requested day, the requested
time must fall within the doctor's availability window, and the doctor must
not already have an appointment at that exact datetime.

---

## Running the Application

### Prerequisites
- Java 17 (Amazon Corretto recommended)
- Maven 3.8+

### Start the application
```bash
./mvnw spring-boot:run
```

### Access points

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| H2 Console | http://localhost:8080/h2-console |

### H2 Console connection settings

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:clinicdb` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## API Overview

### Doctors
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/{id}` | Get doctor by id |
| POST | `/api/doctors` | Create a new doctor |

### Patients
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by id |
| POST | `/api/patients` | Create a new patient |

### Appointments
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/appointments` | Get all appointments |
| GET | `/api/appointments/{id}` | Get appointment by id |
| GET | `/api/appointments/patient/{patientId}` | Get appointments by patient |
| GET | `/api/appointments/doctor/{doctorId}` | Get appointments by doctor |
| POST | `/api/appointments` | Book a new appointment |
| PATCH | `/api/appointments/{id}/cancel` | Cancel an appointment |

---

## Seed Data

The application loads seed data automatically on startup via `data.sql`:

- 3 doctors with different availability schedules
- 4 patients, one without a preferred doctor
- 10 availability slots across the three doctors
- 4 pre-booked appointments for testing

---

## Development Notes

This project uses `spring.jpa.hibernate.ddl-auto=create-drop` which means
the database schema is created fresh on every startup and dropped on shutdown.
All seed data is reloaded on each run. This is intentional for development
purposes.

For production deployment this would be replaced with Flyway migrations and
`ddl-auto=validate`.