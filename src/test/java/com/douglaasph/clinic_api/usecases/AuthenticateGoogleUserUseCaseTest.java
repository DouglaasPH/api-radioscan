package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateSessionUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GoogleTokenVerifierAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.TokenProviderAdapterPort;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.usecases.AuthenticateGoogleUserUseCase;
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
public class AuthenticateGoogleUserUseCaseTest {
    @Mock
    private GoogleTokenVerifierAdapterPort googleTokenVerifierAdapterPort;

    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private TokenProviderAdapterPort tokenProviderAdapterPort;

    @Mock
    private CreateSessionUseCasePort createSessionUseCasePort;

    @InjectMocks
    private AuthenticateGoogleUserUseCase authenticateGoogleUserUseCase;

    private static final String GOOGLETOKEN = "google-token";
    private static final String EMAIL = "douglas@radioscan.com";

    @Test
    @DisplayName("Must authenticate and return tokens when the Google token is valid")
    void case1() {
        // arrange
        when(googleTokenVerifierAdapterPort.verifyAndGetEmail(GOOGLETOKEN)).thenReturn(EMAIL);
        User user = new User(1L, null, EMAIL, null, null, null, null);
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tokenProviderAdapterPort.generateToken(EMAIL)).thenReturn("access-token");
        RefreshToken refreshToken = new RefreshToken(null, "refresh-token", user, null);
        when(createSessionUseCasePort.execute(EMAIL)).thenReturn(refreshToken);

        // act
        LoginResult result = authenticateGoogleUserUseCase.execute(GOOGLETOKEN);

        // assert
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");

        verify(googleTokenVerifierAdapterPort).verifyAndGetEmail(GOOGLETOKEN);
        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(tokenProviderAdapterPort).generateToken(EMAIL);
        verify(createSessionUseCasePort).execute(EMAIL);
    }

    @Test
    @DisplayName("Should propagate exception when email is not found")
    void case2() {
        // arrange
        when(googleTokenVerifierAdapterPort.verifyAndGetEmail(GOOGLETOKEN)).thenReturn(EMAIL);
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> authenticateGoogleUserUseCase.execute(GOOGLETOKEN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@radioscan.com'");

        verify(googleTokenVerifierAdapterPort).verifyAndGetEmail(GOOGLETOKEN);
        verifyNoInteractions(tokenProviderAdapterPort, createSessionUseCasePort);
    }
}
