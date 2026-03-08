package com.project.back_end.service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repository.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

     public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String ,String>> savePrescription(Prescription prescription)
    {
         Map<String, String> response = new HashMap<>();

         try{
            prescriptionRepository.save(prescription);
            response.put("message", "prescription is saved");
            return new ResponseEntity<>(response,HttpStatus.CREATED);
         }
         catch (Exception e) {
            response.put("message", "internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            response.put("prescription", prescriptions);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "interal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
