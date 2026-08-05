package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.UpdateUserDataUseCase;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class UpdateUserDataUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private SaveUserAdapterPort saveUserAdapterPort;

    @InjectMocks
    private UpdateUserDataUseCase updateUserDataUseCase;

    private static final String CPF = "000.000.000-00";
    private static final String PHONE = "+55 81 90000-0000";
    private static final String EMAIL = "douglas@example.com";

    private User createUser() {
        return new User(
                1L,
                null,
                EMAIL,
                null,
                Roles.PATIENT,
                null,
                null
        );
    }

    private Patient patient() {
        return new Patient(
                2L,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("Must return user when credentials are valid")
    void case1() {
        // arrange
        User user = createUser();
        user.setPatient(patient());

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(saveUserAdapterPort.save(user)).thenReturn(user);

        User result = updateUserDataUseCase.execute(CPF, PHONE, EMAIL);

        // assert
        assertThat(result.getPatient().getCpf()).isEqualTo(CPF);
        assertThat(result.getPatient().getPhone()).isEqualTo(PHONE);
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> updateUserDataUseCase.execute(CPF, PHONE, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@example.com'");

        verifyNoInteractions(saveUserAdapterPort);
    }
}
