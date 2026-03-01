package com.project.back_end.models;

import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    @ManyToOne  //many appointment can points doctor 
    @NotNull (message = "doctor is required")
    private Doctor doctor;

    @ManyToOne 
    @NotNull (message = "patient is required")
    private Patient patient;

    @Future(message = "appointment should be in the future")
    @NotNull (message = "appointment time is required")
    private LocalDateTime appointmentTime;

    // 0 = scheduled , 1 = completed
    private int status;


    @Transient
    public LocalDateTime getEndTime()
    {
         if(appointmentTime==null)
    {
        throw new IllegalStateException("error with time ");
    }
         return appointmentTime.plusHours(1);
    }

    @Transient
public LocalDate getAppointmentDate() {
    if(appointmentTime==null)
    {
        throw new IllegalStateException("error with time ");
    }
    return appointmentTime.toLocalDate();

}

@Transient
public LocalTime getAppointmentTimeOnly() {
  if(appointmentTime==null)
    {
        throw new IllegalStateException("error with time ");
    }
    return appointmentTime.toLocalTime();
}


    //setter and getter
    public Long getId()
    {
        return id;

    }
    public void setId(Long id)
    {
        this.id = id;
    }

    public Doctor getDoctor()
    {
        return doctor;
    }
    public void setDoctor(Doctor doctor)
    {
        this.doctor = doctor;
    }
    
     public Patient getPatient ()
    {
        return patient ;
    }
    public void setPatient (Patient patient )
    {
        this.patient = patient;
    }

    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status )
    {
        this.status = status;
    }

    public LocalDateTime getAppointmentTime()
    {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime)
    {
       this.appointmentTime = appointmentTime;
    }


}
