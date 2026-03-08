package com.project.Service;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.repository.AppointmentRepository;
import com.repository.DoctorRepository;
import com.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            Service service
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    public int bookAppointment(Appointment appointment) {
        if (appointment == null) {
            return 0;
        }
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> res = new HashMap<>();
        if (appointment == null || appointment.getId() == null || appointment.getId() <= 0) {
            res.put("message", "Invalid appointment data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
            if (existing.isEmpty()) {
                res.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            int valid = service.validateAppointment(appointment);
            if (valid == -1) {
                res.put("message", "Invalid doctor id");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
            }
            if (valid == 0) {
                res.put("message", "Appointment time slot is not available");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
            }

            appointmentRepository.save(appointment);
            res.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "Failed to update appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> res = new HashMap<>();
        try {
            Optional<Appointment> opt = appointmentRepository.findById(id);
            if (opt.isEmpty()) {
                res.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            String email;
            try {
                email = tokenService.extractIdentifier(token);
            } catch (Exception e) {
                res.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            }
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                res.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            }

            Appointment appt = opt.get();
            if (appt.getPatient() == null || !Objects.equals(appt.getPatient().getId(), patient.getId())) {
                res.put("message", "Forbidden: you can only cancel your own appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
            }

            appointmentRepository.delete(appt);
            res.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "Failed to cancel appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> res = new HashMap<>();
        res.put("appointments", Collections.emptyList());
        res.put("count", 0);

        if (date == null || token == null || token.isBlank()) {
            res.put("message", "Invalid request");
            return res;
        }

        try {
            String doctorEmail;
            try {
                doctorEmail = tokenService.extractIdentifier(token);
            } catch (Exception e) {
                res.put("message", "Unauthorized");
                return res;
            }
            Doctor doctor = doctorRepository.findByEmail(doctorEmail);
            if (doctor == null) {
                res.put("message", "Doctor not found");
                return res;
            }

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

            List<Appointment> list = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);

            if (pname != null && !pname.isBlank() && !"null".equalsIgnoreCase(pname)
                    && !"all".equalsIgnoreCase(pname)) {
                String key = pname.trim().toLowerCase();
                list = list.stream()
                        .filter(a -> a.getPatient() != null
                                && a.getPatient().getName() != null
                                && a.getPatient().getName().toLowerCase().contains(key))
                        .collect(Collectors.toList());
            }

            res.put("appointments", list);
            res.put("count", list.size());
            res.put("doctorId", doctor.getId());
            res.put("date", date.toString());
            return res;
        } catch (Exception e) {
            res.put("message", "Failed to fetch appointments");
            return res;
        }
    }
}
