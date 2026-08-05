package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokensByUserIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.CreateSessionUseCase;
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

@ExtendWith(MockitoExtension.class)
public class CreateSessionUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private DeleteRefreshTokensByUserIdAdapterPort deleteRefreshTokensByUserIdAdapterPort;

    @Mock
    private CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;

    @InjectMocks
    private CreateSessionUseCase createSessionUseCase;

    private static final String EMAIL = "douglas@gmail.com";

    @Test
    @DisplayName("Must return refresh token when credentials are valid")
    void case1() {
        // arrange
        User user = new User(
                null,
                null,
                EMAIL,
                null,
                null,
                null,
                null
        );
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
       RefreshToken refreshToken = new RefreshToken(
               null,
               "token",
               user,
               null
       );
       when(createRefreshTokenAdapterPort.create(EMAIL)).thenReturn(refreshToken);

        // act
        RefreshToken result = createSessionUseCase.execute(EMAIL);

        // assert
        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getUser().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> createSessionUseCase.execute(EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@gmail.com'");

        verifyNoInteractions(deleteRefreshTokensByUserIdAdapterPort);
        verifyNoInteractions(createRefreshTokenAdapterPort);
    }
}
