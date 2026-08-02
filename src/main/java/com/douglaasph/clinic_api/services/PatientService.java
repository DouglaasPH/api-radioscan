package com.douglaasph.clinic_api.services;

import com.douglaasph.clinic_api.controllers.dto.auth.LoginResponseDto;
import com.douglaasph.clinic_api.controllers.dto.patient.RegisterPatientDto;
import com.douglaasph.clinic_api.models.entities.Patient;
import com.douglaasph.clinic_api.models.entities.RefreshToken;
import com.douglaasph.clinic_api.models.entities.User;
import com.douglaasph.clinic_api.models.entities.enums.Roles;
import com.douglaasph.clinic_api.repositories.PatientRepository;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import com.douglaasph.clinic_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JWTService jwtService;

    @Transactional
    public LoginResponseDto register(RegisterPatientDto dto) {
        try {
            User user = new User(null,
                    dto.user().name(),
                    dto.user().email(),
                    passwordEncoder.encode(dto.user().password()),
                    Roles.valueOf(3));
            User savedUser = userRepository.save(user);

            Patient patient = new Patient(null,
                    dto.patient().cpf(),
                    dto.patient().phone(),
                    savedUser);

            patientRepository.save(patient);

            String accessToken = jwtService.generateToken(user.getEmail());
            RefreshToken refreshToken = refreshTokenService.insert(user.getEmail());

            return new LoginResponseDto(true, accessToken, refreshToken.getToken());

        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Registration error: Email or document may already exist.");
        }
    }
}
