package com.project.back_end.service;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.PatientRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        try {
            String identifier = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(identifier);

            if (patient == null || patient.getId() == null || !Objects.equals(patient.getId(), id)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", (Object) "Unauthorized"));
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<Appointment> dtos = toAppointmentDTOs(appointments);

            Map<String, Object> body = new HashMap<>();
            body.put("appointments", dtos);
            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", (Object) "Internal server error"));
        }
    }

      public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        try {
            Integer status = statusFromCondition(condition);
            if (status == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", (Object) "Invalid condition. Use 'past' or 'future'."));
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

            Map<String, Object> body = new HashMap<>();
            body.put("appointments", toAppointmentDTOs(appointments));
            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", (Object) "Internal server error"));
        }
    }

    //Filter patient appointments by doctor name.
    public ResponseEntity<Map<String,Object>>filterByDoctor(String name,Long patientId)
    {
        try{
            if(name == null|| name.isBlank()||"null".equalsIgnoreCase(name))
            {
                //if no doctor name , list all appointment for this patient
                List<Appointment>appointments=appointmentRepository.findByPatientId(patientId);
                return ResponseEntity.ok(Map.of("appointments", (Object) toAppointmentDTOs(appointments)));
            }

            List<Appointment>appointments=appointmentRepository.filterByDoctorNameAndPatientId(name,patientId);

            Map<String, Object>body =new HashMap<>();
            body.put("appointments",toAppointmentDTOs(appointments));
            return ResponseEntity.ok(body);
        }catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", (Object) "Internal server error"));
        }

    }

    public ResponseEntity<Map<String,Object>>filterByDoctorAndCondition(String condition,String name,Long patientId)
{
    try{
        Integer status = statusFromCondition(condition);
        if(status==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", (Object) "Invalid condition. Use 'past' or 'future'."));

        }
        if (name == null || name.isBlank() || "null".equalsIgnoreCase(name)) {
        List<Appointment> appointments =
        appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
        return ResponseEntity.ok(Map.of("appointments", (Object) toAppointmentDTOs(appointments)));
            }

        List<Appointment> appointments =
        appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        Map<String, Object> body = new HashMap<>();
        body.put("appointments", toAppointmentDTOs(appointments));
        return ResponseEntity.ok(body);


    }

    catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", (Object) "Internal server error"));
    }


}

public ResponseEntity<Map<String,Object>>getPatientDetails(String token)
{
    try{
        String identifier = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(identifier);
         if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", (Object) "Patient not found"));
            }

            Map<String,Object>body = new HashMap<>();
            body.put("patient",patient);
             return ResponseEntity.ok(body);

    }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", (Object) "Internal server error"));
        }


}

//helper function 

private Integer statusFromCondition(String condition)
{
    if(condition==null)
    {
        return null;
    }
    String c =condition.trim().toLowerCase();
     if ("past".equals(c)) return 1;
        if ("future".equals(c)) return 0;
        return null;
}

private List<Appointment> toAppointmentDTOs(List<Appointment> appointments) {
        if (appointments == null) return List.of();
        return appointments;
    }


private Appointment toAppointmentDTO(Appointment a)
{
    return a;
}


}
