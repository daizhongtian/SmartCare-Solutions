

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository PatientRepository;
    private final DoctorRepository DoctorRepository;
    private final TokenService tokenService;
    private final ServiceValidation service;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              ServiceValidation service)
    {
        this.appointmentRepository=appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
     }



   public int bookAppointment(Appointment appointment)
   {
    if (appointment==null)
    {
        return 0;
    }
    try{
        AppointmentRepository.save(appointment);
        return 1;
    }
    catch(Exception e)
    {
        return 0;
    }

   }

   public ResponseEntity <Map<String,String>> updateAppointment(Appointment appointment)
   {
    Map<String ,String> res = new HashMap<>();
    if(appointment==null || appointment.getId()<=0)
    {
        res.put("message","Invalid appointment data");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    try{
        //optional - this value can have or be empty 
        Optional<Appointment> existing =AppointmentRepository.findById(appointment.getId());
        if(existing.isEmpty())
        {
            res.put("message","appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
          
        //check doctor is valid id and not busy 
        int valid = service.validateAppointment(appointment);
        if(Valid ==-1)
        {
            res.put("message","invalid doctor id");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }

          if (valid == 0) {
            res.put("message", "Appointment time slot is not available");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }

        appointmentRepository.save(appointment);
        res.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(res);
    }

    catch(Exception e)
    {
        res.put("message","Failed to update appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).bodt(res);
    }

   }



   //toke to get the userid for person who requested this action 
   public ResponseEntity<Map<String,String>>cancelAppointment(long id,String token)
   {
    try{
        Optional<Appointment>opt = appointmentRepository.findById(id);
        if(opt.isEmpty())
        {
            res.put("message","Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);

        }
        Appointment appt = opt.get();

        String email=tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);

        if(patient ==null)
        {
             res.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        if(appt.getPatientId()==null||!Object.equals(appt.getPatientId(),patient.getId())) //compare patient id in appointment and requested
        {
            res.put("message", "Forbidden: you can only cancel your own appointment");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        appointmentRepository.delete(appt);
        res.put("message","appointment cancelled successfully");

    }
    catch(Exception e)
    {
        res.put("message ","Failed to cancel appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }


   }
   

   //get the doctor's appointment in specific day;
   public Map<String,Object>getAppointment(String pname,LocalDate date,String token)
   {
    Map<String,Object> res = new HashMap<>();
    res.put("appointments",Collections.emptyList());   //collection for empty set
    res.put("count",0); //return empty as a default
    
    if(date==null||token==null||token.isBlank())
    {
        res.put("message", "Invalid request");
            return res;
    }

    try{
        String doctorEmail = tokenService.extractIdentifier(token);
        Doctor doctor =doctorRepository.findByEmail(doctorEmail);

        if(doctor ==null)
        {
            res.put("message","doctor not found");
            return res;
        }

        LocalDateTime start =date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Appointment> list = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(),start,end);

        if(pname!=null && !pname.isBlank&& !"null".equalsIgnoreCase(pname)&& !"all".equalsIgnoreCase(pname)) {

            String Key = pname.trim().toLowerCase();
            list=list.stream().filter(a->a.getPatientName()!=null&&a.getPatientName().toLowerCase().contains(key).collect(Collectors.toList()));
        }

        res.put("aappointments",list);
        res.put("count",list.size());
        res.put("doctorId", doctor.getId());
        res.put("date", date.toString());
        return res;

    }

    catch(Exception e)
    {
        res.put("message", "Failed to fetch appointments");
        return res;
    }







   }



}
