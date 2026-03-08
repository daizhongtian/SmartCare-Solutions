package com.project.Service;

import com.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.repository.AdminRepository;
import com.repository.DoctorRepository;
import com.repository.PatientRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.stereotype.Service  // use full name to prevent the name confilict ("service")
public class Service {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;


     public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;

    }

 public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        if (token == null || token.isBlank() || !tokenService.validateToken(token, user)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return null;
    }

    public ResponseEntity<Map<String,String>>validateAdmin(Admin receivedAdmin)
    {
        Map<String,String>response = new HashMap<>();

        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if(admin==null||!admin.getPassword().equals(receivedAdmin.getPassword()))
        {
            response.put("message","invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = tokenService.generateToken(admin.getUsername());
        response.put("token",token);
        response.put("message", "Admin login successful");
        return ResponseEntity.ok(response);

    }

        public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = hasValue(name);
        boolean hasSpecialty = hasValue(specialty);
        boolean hasTime = hasValue(time);

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecialtyAndTime(name, specialty, time);
        } else if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecialty(name, specialty);
        } else if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecialty(specialty, time);
        } else if (hasName) {
            return doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            return doctorService.filterDoctorBySpecialty(specialty);
        } else if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public int validateAppointment(Appointment appointment)
    {
        if (appointment == null
                || appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null
                || appointment.getAppointmentTime() == null) {
            return -1;
        }


        Long doctorId = appointment.getDoctor().getId();
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        if(doctorOptional.isEmpty())
        {
            return -1;
        }
        LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
        String appointmentStartTime = appointment.getAppointmentTime()
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        boolean isAvailable = availableSlots.stream().anyMatch(slot -> slot != null && slot.startsWith(appointmentStartTime));
        return isAvailable?1:0;
    }
    public boolean validatePatient(Patient patient)
    {
        Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(),patient.getPhone());
        return existingPatient==null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
    Map<String, String> response = new HashMap<>();

    Patient patient = patientRepository.findByEmail(login.getIdentifier());

    if (patient == null || !patient.getPassword().equals(login.getPassword())) {
        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String token = tokenService.generateToken(patient.getEmail());
    response.put("token", token);
    response.put("message", "Patient login successful");

    return ResponseEntity.ok(response);
}


 public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long patientId = patient.getId();

        boolean hasCondition = hasValue(condition);
        boolean hasName = hasValue(name);

        if (hasCondition && hasName) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        } else if (hasCondition) {
            return patientService.filterByCondition(condition, patientId);
        } else if (hasName) {
            return patientService.filterByDoctor(name, patientId);
        }

        return patientService.getPatientAppointment(patientId, token);
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank() && !"null".equalsIgnoreCase(value);
    }



} 



