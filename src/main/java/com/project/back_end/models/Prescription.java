package com.project.back_end.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;

    @NotNull(message = "patientName cannot be empty")
    @Size(min = 3 ,max = 100)
    private String patientName;

    @NotNull(message = "appointment id cannot be empty")
    private Long appointmentId;

    @NotNull(message = "medication cannot be empty")
    @Size(min = 3, max = 100)
    private String medication;

    @NotNull(message = "dosage cannot be empty")
    @Size(min = 3, max = 20)
    private String dosage;
  

    @Size(max = 200)
    private String doctorNotes;



//getter and setter
    public String getId() {
        return id;
    }   

     public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medidation) {
        this.medication = medidation;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}