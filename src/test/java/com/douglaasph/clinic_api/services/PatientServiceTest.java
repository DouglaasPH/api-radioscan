package com.douglaasph.clinic_api.services;

import com.douglaasph.clinic_api.controllers.dto.auth.LoginResponseDto;
import com.douglaasph.clinic_api.controllers.dto.patient.PatientDto;
import com.douglaasph.clinic_api.controllers.dto.patient.RegisterPatientDto;
import com.douglaasph.clinic_api.controllers.dto.user.UserDto;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import com.douglaasph.clinic_api.models.entities.Patient;
import com.douglaasph.clinic_api.models.entities.RefreshToken;
import com.douglaasph.clinic_api.models.entities.User;
import com.douglaasph.clinic_api.models.entities.enums.Roles;
import com.douglaasph.clinic_api.repositories.PatientRepository;
import com.douglaasph.clinic_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JWTService jwtService;
    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setup() { MockitoAnnotations.initMocks(this); }

    @Test
    @DisplayName("Should register patient successfully when data is valid")
    void registerCase1() {
        UserDto userDto = new UserDto("Douglas Phelipe", "example@gmail.com", "1234");
        PatientDto patientDto = new PatientDto("12345678912", "81900000000");
        RegisterPatientDto registerPatientDto = new RegisterPatientDto(userDto, patientDto);

        User user = new User(null, userDto.name(), userDto.email(), "mocked-encode", Roles.PATIENT);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        Patient patient = new Patient(null, patientDto.cpf(), patientDto.phone(), user);
        Mockito.when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Mockito.when(jwtService.generateToken("example@gmail.com")).thenReturn("mocked-access-token");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mocked-refresh-token");
        Mockito.when(refreshTokenService.insert(anyString())).thenReturn(mockRefreshToken);

        LoginResponseDto response = patientService.register(registerPatientDto);

        assertNotNull(response);
        assertTrue(response.registered());
        assertEquals("mocked-access-token", response.accessToken());
        assertEquals("mocked-refresh-token", response.refreshToken());

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verify(patientRepository, Mockito.times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw DatabaseException when email or document already exists")
    void registerCase2() {
        UserDto userDto = new UserDto("Douglas Phelipe", "example@gmail.com", "1234");
        PatientDto patientDto = new PatientDto("12345678912", "81900000000");
        RegisterPatientDto registerPatientDto = new RegisterPatientDto(userDto, patientDto);

        Mockito.when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            patientService.register(registerPatientDto);
        });

        assertEquals("Registration error: Email or document may already exist.", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verifyNoInteractions(patientRepository);
    }
}