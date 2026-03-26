INSERT INTO doctors (full_name, email, phone_number, specialty, license_number, created_at, updated_at)
VALUES
    ('Dr. Sarah Mitchell', 'sarah.mitchell@clinic.com', '555-0101', 'General Practice', 'GP-10001', NOW(), NOW()),
    ('Dr. James Okafor', 'james.okafor@clinic.com', '555-0102', 'General Practice', 'GP-10002', NOW(), NOW()),
    ('Dr. Linda Tran', 'linda.tran@clinic.com', '555-0103', 'General Practice', 'GP-10003', NOW(), NOW());

INSERT INTO patients (full_name, date_of_birth, email, phone_number, preferred_doctor_id, created_at, updated_at)
VALUES
    ('Alice Barnes', '1990-04-12', 'alice.barnes@email.com', '555-0201', 1, NOW(), NOW()),
    ('Robert Chen', '1985-08-23', 'robert.chen@email.com', '555-0202', 2, NOW(), NOW()),
    ('Maria Gonzalez', '1978-11-05', 'maria.gonzalez@email.com', '555-0203', 3, NOW(), NOW()),
    ('Tom Higgins', '2000-01-30', 'tom.higgins@email.com', '555-0204', NULL, NOW(), NOW());

INSERT INTO doctor_availability (doctor_id, day_of_week, start_time, end_time, created_at, updated_at)
VALUES
    (1, 'MONDAY', '09:00', '17:00', NOW(), NOW()),
    (1, 'WEDNESDAY', '09:00', '17:00', NOW(), NOW()),
    (1, 'FRIDAY', '09:00', '13:00', NOW(), NOW()),
    (2, 'TUESDAY', '08:00', '16:00', NOW(), NOW()),
    (2, 'THURSDAY', '08:00', '16:00', NOW(), NOW()),
    (3, 'MONDAY', '10:00', '18:00', NOW(), NOW()),
    (3, 'TUESDAY', '10:00', '18:00', NOW(), NOW()),
    (3, 'WEDNESDAY', '10:00', '18:00', NOW(), NOW()),
    (3, 'THURSDAY', '10:00', '18:00', NOW(), NOW()),
    (3, 'FRIDAY', '10:00', '14:00', NOW(), NOW());

INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, reason, status, created_at, updated_at)
VALUES
    (1, 1, '2025-04-07 09:00:00', 'Annual checkup', 'BOOKED', NOW(), NOW()),
    (2, 2, '2025-04-08 08:30:00', 'Follow-up on blood pressure', 'BOOKED', NOW(), NOW()),
    (3, 3, '2025-04-09 10:00:00', 'Flu symptoms', 'BOOKED', NOW(), NOW()),
    (4, 1, '2025-04-11 11:00:00', 'Back pain consultation', 'BOOKED', NOW(), NOW());