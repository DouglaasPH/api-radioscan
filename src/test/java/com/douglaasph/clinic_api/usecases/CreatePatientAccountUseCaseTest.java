package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.usecases.CreatePatientAccountUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePatientAccountUseCaseTest {
    @Mock
    private SaveUserAdapterPort saveUserAdapterPort;

    @Mock
    private SavePatientAdapterPort savePatientAdapterPort;

    @Mock
    private PasswordEncoderAdapterPort passwordEncoderAdapterPort;

    @Mock
    private TokenProviderAdapterPort tokenProviderAdapterPort;

    @Mock
    private CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;

    @InjectMocks
    private CreatePatientAccountUseCase createPatientAccountUseCase;

    private static final String NAME = "Douglas Phelipe";
    private static final String EMAIL = "douglas@gmail.com";
    private static final String PASSWORD = "password";
    private static final String CPF = "000.000.000-00";
    private static final String PHONE = "+55 81 90000-0000";

    @Test
    @DisplayName("Must return employee when credentials are valid")
    void case1() {
        // arrange
        User user = new User(
                null,
                NAME,
                EMAIL,
                PASSWORD,
                Roles.EMPLOYEE,
                null,
                null
        );
        when(passwordEncoderAdapterPort.encode(PASSWORD)).thenReturn(PASSWORD);
        when(saveUserAdapterPort.save(any(User.class))).thenReturn(user);
        Patient patient = new Patient(
                null,
                CPF,
                PHONE,
                user
        );
        when(savePatientAdapterPort.save(any(Patient.class))).thenReturn(patient);
        String accessToken = "access-token";
        when(tokenProviderAdapterPort.generateToken(EMAIL)).thenReturn(accessToken);
        RefreshToken refreshToken = new RefreshToken(
                null,
                "refresh-token",
                user,
                null
        );
        when(createRefreshTokenAdapterPort.create(EMAIL)).thenReturn(refreshToken);

        // act
        LoginResult result = createPatientAccountUseCase.execute(NAME, EMAIL, PASSWORD, CPF, PHONE);

        // assert
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
    }

}
