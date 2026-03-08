# Schema Design

## Overview
This project uses MySQL as the main relational database for managing admins, doctors, patients, doctor available time, and appointments.

The database is designed to support:
- Admin management
- Doctor information management
- Patient registration and profile storage
- Doctor available time management
- Appointment booking and tracking

## Tables

### 1. ADMIN
Stores administrator login information.

| Field | Type | Constraints | Description |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique admin ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Admin username |
| password | VARCHAR(255) | NOT NULL | Admin password |

---

### 2. DOCTOR
Stores doctor account and profile information.

| Field | Type | Constraints | Description |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique doctor ID |
| name | VARCHAR(100) | NOT NULL | Doctor name |
| specialty | VARCHAR(50) | NOT NULL | Doctor specialty |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Doctor email |
| password | VARCHAR(255) | NOT NULL | Doctor password |
| phone | VARCHAR(20) | NOT NULL | Doctor phone number |

---

### 3. PATIENT
Stores patient account and profile information.

| Field | Type | Constraints | Description |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique patient ID |
| name | VARCHAR(100) | NOT NULL | Patient name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Patient email |
| password | VARCHAR(255) | NOT NULL | Patient password |
| phone | VARCHAR(20) | NOT NULL | Patient phone number |
| address | VARCHAR(255) | NOT NULL | Patient address |

---

### 4. DOCTOR_AVAILABLE_TIME
Stores doctor available time slots.

| Field | Type | Constraints | Description |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique record ID |
| doctor_id | BIGINT | NOT NULL, FOREIGN KEY | References DOCTOR(id) |
| time_slot | VARCHAR(50) | NOT NULL | Available time slot, e.g. 09:00 - 10:00 |

---

### 5. APPOINTMENT
Stores appointment records between patients and doctors.

| Field | Type | Constraints | Description |
|---|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique appointment ID |
| doctor_id | BIGINT | NOT NULL, FOREIGN KEY | References DOCTOR(id) |
| patient_id | BIGINT | NOT NULL, FOREIGN KEY | References PATIENT(id) |
| appointment_time | DATETIME | NOT NULL | Appointment date and time |
| status | INT | NOT NULL | Appointment status (0 = Scheduled, 1 = Completed) |

---

## Relationships

### ADMIN
- ADMIN is independent and used for platform administration.

### DOCTOR and DOCTOR_AVAILABLE_TIME
- One doctor can have multiple available time slots.
- `DOCTOR_AVAILABLE_TIME.doctor_id` is a foreign key referencing `DOCTOR.id`.

### DOCTOR and APPOINTMENT
- One doctor can have many appointments.
- `APPOINTMENT.doctor_id` is a foreign key referencing `DOCTOR.id`.

### PATIENT and APPOINTMENT
- One patient can have many appointments.
- `APPOINTMENT.patient_id` is a foreign key referencing `PATIENT.id`.

## Entity Relationship Summary
- ADMIN: standalone table for admin authentication
- DOCTOR: stores doctor information
- PATIENT: stores patient information
- DOCTOR_AVAILABLE_TIME: stores doctor schedule slots
- APPOINTMENT: links doctor and patient for booking records

## Notes
- All primary keys use `AUTO_INCREMENT`.
- Email fields are designed to be unique.
- Password fields are stored as string values in the database.
- Appointment records connect doctors and patients through foreign keys.
- The schema supports the main project features such as login, doctor management, patient registration, available time management, and appointment booking.
