package com.project.back_end.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // find specific doctor's all appointment between specific period
    @Query("SELECT DISTINCT a " +
            "FROM Appointment a " +
            "LEFT JOIN FETCH a.doctor d " +
            "LEFT JOIN FETCH d.availableTimes av " +
            "WHERE d.id = :doctorId " +
            "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end

    );

    //-------------
    @Query("SELECT DISTINCT a " +
            "FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH a.doctor d " +
            "WHERE d.id = :doctorId " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " + // means %patient name% - Fuzzy search
            "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    //------------

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);

    //-------
    @Query("SELECT a " +
            "FROM Appointment a " +
            "LEFT JOIN FETCH a.doctor d " +
            "LEFT JOIN FETCH a.patient p " +
            "WHERE p.id = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    //--

    @Query("SELECT DISTINCT a " +
            "FROM Appointment a " +
            "LEFT JOIN FETCH a.doctor d " +
            "LEFT JOIN FETCH a.patient p " +
            "WHERE p.id = :patientId " +
            "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId
    );

    //---
    @Query("SELECT DISTINCT a " +
            "FROM Appointment a " +
            "LEFT JOIN FETCH a.doctor d " +
            "LEFT JOIN FETCH a.patient p " +
            "WHERE p.id = :patientId " +
            "AND a.status = :status " +
            "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId,
            @Param("status") int status
    );

}
