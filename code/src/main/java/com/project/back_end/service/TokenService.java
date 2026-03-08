package com.project.back_end.service;

import com.project.back_end.repository.AdminRepository;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.repository.PatientRepository;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${secret}")
    private String secret;

      public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    //generate token 
    public String generateToken(String identifier)
    {
        Date now = new Date();
        Date exp = new Date(now.getTime()+ 7L*24*60*1000);
        
        return Jwts.builder().setSubject(identifier).setSubject(identifier) .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    //

    public String extractIdentifier(String token)
    {
        String rawToken = token != null && token.startsWith("Bearer ")
                ? token.substring(7)
                : token;
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
        .parseClaimsJws(rawToken)
                .getBody()
                .getSubject();
        
    }

    public boolean validateToken(String token,String user)
    {
        try{
            String identifier = extractIdentifier(token);

            if("admin".equalsIgnoreCase(user))
            {
                return adminRepository.findByUsername(identifier)!=null;

            }
              if ("doctor".equalsIgnoreCase(user)) {
                return doctorRepository.findByEmail(identifier) != null;
            }
            if ("patient".equalsIgnoreCase(user)) {
                return patientRepository.findByEmail(identifier) != null;
            }
            return false;

        }
        catch (JwtException e)
        {
            return false;
        }
    }



       private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

