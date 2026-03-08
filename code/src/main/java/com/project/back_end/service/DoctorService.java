package com.project.back_end.service;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.DoctorRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        if (doctorId == null || date == null) {
            return Collections.emptyList();
        }

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null || doctor.getAvailableTimes() == null) {
            return Collections.emptyList();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appts = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        Set<String> booked = appts.stream()
                .map(a -> toSlot(a.getAppointmentTime()))
                .collect(Collectors.toSet());

        return doctor.getAvailableTimes().stream()
                .filter(Objects::nonNull)
                .map(DoctorService::clean)
                .filter(slot -> !booked.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctor == null || doctor.getEmail() == null) {
                return 0;
            }
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor == null || doctor.getId() == null) {
                return 0;
            }
            if (doctorRepository.findById(doctor.getId()).isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            if (doctorRepository.findById(id).isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> body = new HashMap<>();
        try {
            if (login == null || isBlank(login.getIdentifier()) || isBlank(login.getPassword())) {
                body.put("message", "Missing credentials");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            if (doctor == null || !Objects.equals(doctor.getPassword(), login.getPassword())) {
                body.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            body.put("token", tokenService.generateToken(doctor.getEmail()));
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            body.put("message", "Internal error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> res = new HashMap<>();
        List<Doctor> doctors = isBlank(name) ? doctorRepository.findAll() : doctorRepository.findByNameLike(name);
        res.put("doctors", doctors);
        return res;
    }

    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors;
        if (!isBlank(name) && !isBlank(specialty)) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (!isBlank(name)) {
            doctors = doctorRepository.findByNameLike(name);
        } else if (!isBlank(specialty)) {
            doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } else {
            doctors = doctorRepository.findAll();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = isBlank(name) ? doctorRepository.findAll() : doctorRepository.findByNameLike(name);
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        List<Doctor> doctors;
        if (!isBlank(name) && !isBlank(specialty)) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (!isBlank(name)) {
            doctors = doctorRepository.findByNameLike(name);
        } else if (!isBlank(specialty)) {
            doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } else {
            doctors = doctorRepository.findAll();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctors);
        return res;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        List<Doctor> doctors = isBlank(specialty)
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyIgnoreCase(specialty);

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", isBlank(specialty)
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return res;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctorRepository.findAll(), amOrPm));
        return res;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (doctors == null || isBlank(amOrPm)) {
            return doctors == null ? Collections.emptyList() : doctors;
        }

        String v = amOrPm.trim().toLowerCase();
        boolean wantAM = v.equals("am") || v.contains("morning");
        boolean wantPM = v.equals("pm") || v.contains("afternoon");
        if (!wantAM && !wantPM) {
            return doctors;
        }

        return doctors.stream().filter(d -> {
            List<String> avail = d.getAvailableTimes();
            if (avail == null) {
                return false;
            }
            for (String slot : avail) {
                Integer h = startHour(slot);
                if (h == null) {
                    continue;
                }
                if (wantAM && h < 12) {
                    return true;
                }
                if (wantPM && h >= 12) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private static String toSlot(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return clean(HHMM.format(time.toLocalTime()) + "-" + HHMM.format(time.toLocalTime().plusHours(1)));
    }

    private static Integer startHour(String slot) {
        try {
            if (slot == null) {
                return null;
            }
            String start = clean(slot).split("-")[0];
            return Integer.parseInt(start.split(":")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim().replace(" ", "").replace("\u2013", "-").replace("\u2014", "-");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
