
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;


    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

        public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    //get the specific doctor specific day avaliable time 
    public List<String> getDoctorAvailability(Long doctorId,LocalDate date)
    {
        if(doctorId==null||date==null)
        {
            return Collections.emptyList();
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null || doctor.getAvailability() == null) return Collections.emptyList();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appts = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId,start,end);


        //build string to collect all booked doctor in this day and then use filter show avaliable time 
        //ex:  avaliability :["09:00-10:00", "10:00-11:00", "11:00-12:00", "14:00-15:00"]
               //booked: ["10:00-11:00", "14:00-15:00"]
               //returned value :["09:00-10:00", "11:00-12:00"]
        set<String> booked = apps.stream().map(a->toSlot(a.getAppointmentTime())).collect(Collectors.toList());
        return doctor.getAvailability().stream().filter(Objects::nonNull).filter(slot->!booked.contains(clean(slot))).collect(Collectors.toList());
    } 

public int saveDoctor(Doctor doctor)
{
    try{
        if(doctor ==null|| doctor.getEmail()==null)
        {
            return 0;
        }
        if(doctorRepository.findByEmail(doctor.getEmail()!=null))
        {
            return -1;
        }
        doctorRepository.save(doctor);
        return 1;
    }
    catch(Exception e)
    {
        return 0;
    }
}


public int updateDoctor(Doctor doctor)
{
    try{
        if(doctor==null||doctor.getId()==null)
        {
            return 0;
        }
        if(doctorRepository.findById(doctor.getId().isEmpty()))
        {
            return -1;
        }
         doctorRepository.save(doctor);
            return 1;
    }
    catch(Exception e)
    {
        return 0;
    }
}


public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

 public int deleteDoctor(long id) {
        try {
            if (doctorRepository.findById(id).isEmpty()) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }


public ResponseEntity<Map<String,String>>validateDoctor(Login login)
{
    Map<String,String> body = new HashMap<>();
    try{
        if(login==null||isBlank(login.getIdentifier())||isBlank(login.getPassword()))
        {
            body.put("message", "Missing credentials");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);

        }
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if(doctor ==null||!Objects.equals(doctor.getPassword(),login.getPassword()))
        {
             body.put("message", "Invalid email or password");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);

        }

        body.put("token",tokenService.generateToken(doctor.getEmail()));
        return ResponseEntity.ok(body);
    }
    catch (Exception e) {
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

public Map<String,Object>filterDoctorsByNameSpecilityandTime(String name, String specialty,String amOrPm)
{
    List<Doctor> doctors;
    if(!isBlank(name)&&!isBlank(specialty))
    {
        doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);      
        
    }
    else if (!isBlank(name)) {
            doctors = doctorRepository.findByNameLike(name);
}
 else
 {
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


    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        List<Doctor> doctors;
        if (!isBlank(name) && !isBlank(specilty)) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        } else if (!isBlank(name)) {
            doctors = doctorRepository.findByNameLike(name);
        } else if (!isBlank(specilty)) {
            doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        } else {
            doctors = doctorRepository.findAll();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctors);
        return res;
    }

     public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        List<Doctor> doctors = isBlank(specilty)
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyIgnoreCase(specilty);

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return res;
    }

    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", isBlank(specilty)
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyIgnoreCase(specilty));
        return res;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", filterDoctorByTime(doctorRepository.findAll(), amOrPm));
        return res;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (doctors == null || isBlank(amOrPm)) return doctors == null ? Collections.emptyList() : doctors;

        String v = amOrPm.trim().toLowerCase();
        boolean wantAM = v.equals("am") || v.contains("morning") || v.equals("上午");
        boolean wantPM = v.equals("pm") || v.contains("afternoon") || v.equals("下午");
        if (!wantAM && !wantPM) return doctors;

        return doctors.stream().filter(d -> {
            List<String> avail = d.getAvailability();
            if (avail == null) return false;

            for (String slot : avail) {
                Integer h = startHour(slot);
                if (h == null) continue;
                if (wantAM && h < 12) return true;
                if (wantPM && h >= 12) return true;
            }
            return false;
        }).collect(Collectors.toList());
    }



//helper fuction

//it do the start time as a slot 
private static String toSlot(LocalDateTime t) {
        if (t == null) return "";
        return clean(HHMM.format(t.toLocalTime()) + "-" + HHMM.format(t.toLocalTime().plusHours(1)));
    }

  private static Integer startHour(String slot) {
    if (slot == null) return null;
    String start = clean(slot).split("-")[0];
    return Integer.parseInt(start.split(":")[0]);
}

    private static String clean(String s) {
        return s == null ? "" : s.trim().replace(" ", "").replace("–", "-").replace("—", "-");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

} 
  