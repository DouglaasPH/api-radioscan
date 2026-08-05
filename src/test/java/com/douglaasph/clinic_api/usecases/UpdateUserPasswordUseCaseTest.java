package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.PasswordEncoderAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.UpdateUserPasswordUseCase;
import com.douglaasph.clinic_api.exceptions.InvalidPasswordException;
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
public class UpdateUserPasswordUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private SaveUserAdapterPort saveUserAdapterPort;

    @Mock
    private PasswordEncoderAdapterPort passwordEncoderAdapterPort;

    @InjectMocks
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    private static final String EMAIL = "douglas@example.com";
    private static final String CURRENT_PASSWORD = "current_password";
    private static final String NEW_PASSWORD = "new_password";

    private User createUser() {
        return new User(
                1L,
                null,
                EMAIL,
                CURRENT_PASSWORD,
                Roles.PATIENT,
                null,
                null
        );
    }

    @Test
    @DisplayName("Must return user when credentials are valid")
    void case1() {
        // arrange
        User user = createUser();
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoderAdapterPort.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoderAdapterPort.encode(NEW_PASSWORD)).thenReturn("new_password_encoded");
        when(saveUserAdapterPort.save(user)).thenReturn(user);

        // act
        User result = updateUserPasswordUseCase.execute(EMAIL, CURRENT_PASSWORD, NEW_PASSWORD);

        // assert
        assertThat(result.getPassword()).isEqualTo("new_password_encoded");

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(passwordEncoderAdapterPort).matches(CURRENT_PASSWORD, CURRENT_PASSWORD);
        verify(passwordEncoderAdapterPort).encode(NEW_PASSWORD);
        verify(saveUserAdapterPort).save(user);
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> updateUserPasswordUseCase.execute(EMAIL, CURRENT_PASSWORD, NEW_PASSWORD))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@example.com'");

        verifyNoInteractions(passwordEncoderAdapterPort);
        verifyNoInteractions(saveUserAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when current password must be different")
    void case3() {
        // arrange
        User user = createUser();
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        when(passwordEncoderAdapterPort.matches("DIFFERENT_PASSWORD", user.getPassword())).thenReturn(false);

        // act + assert
        assertThatThrownBy(() -> updateUserPasswordUseCase.execute(EMAIL, "DIFFERENT_PASSWORD", NEW_PASSWORD))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("The current password is incorrect.");

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(passwordEncoderAdapterPort).matches("DIFFERENT_PASSWORD", user.getPassword());
        verifyNoInteractions(saveUserAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when current password is equal new password")
    void case4() {
        // arrange
        User user = createUser();
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        when(passwordEncoderAdapterPort.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);

        // act + assert
        assertThatThrownBy(() -> updateUserPasswordUseCase.execute(EMAIL, CURRENT_PASSWORD, CURRENT_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The new password must be different from the current password.");

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(passwordEncoderAdapterPort).matches(CURRENT_PASSWORD, user.getPassword());
        verifyNoInteractions(saveUserAdapterPort);
    }
}
