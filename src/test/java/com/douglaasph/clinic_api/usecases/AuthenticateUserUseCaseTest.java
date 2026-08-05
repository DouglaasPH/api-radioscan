package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateSessionUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.CredentialsAuthenticatorAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.TokenProviderAdapterPort;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.usecases.AuthenticateUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticateUserUseCaseTest {
    @Mock
    private CredentialsAuthenticatorAdapterPort credentialsAuthenticatorAdapterPort;

    @Mock
    private TokenProviderAdapterPort tokenProviderAdapterPort;

    @Mock
    private CreateSessionUseCasePort createSessionUseCasePort;

    @InjectMocks
    private AuthenticateUserUseCase authenticateUserUseCase;

    private static final String EMAIL = "douglas@radioscan.com";
    private static final String PASSWORD = "senha123";

    @Test
    @DisplayName("Must authenticate and return tokens when credentials are valid")
    void case1() {
        // arrange
        when(tokenProviderAdapterPort.generateToken(EMAIL)).thenReturn("access-token-mock");
        RefreshToken refreshToken = new RefreshToken(1L, "refresh-token-mock", null, null);
        when(createSessionUseCasePort.execute(EMAIL)).thenReturn(refreshToken);

        // act
        LoginResult result = authenticateUserUseCase.execute(EMAIL, PASSWORD);

        // assert
        assertThat(result.getAccessToken()).isEqualTo("access-token-mock");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-mock");

        verify(credentialsAuthenticatorAdapterPort).authenticate(EMAIL, PASSWORD);
        verify(tokenProviderAdapterPort).generateToken(EMAIL);
        verify(createSessionUseCasePort).execute(EMAIL);
    }

    @Test
    @DisplayName("Should propagate exception when credentials are invalid")
    void case2() {
        // arrange
        doThrow(new BadCredentialsException("Email or password invalid"))
                .when(credentialsAuthenticatorAdapterPort).authenticate(EMAIL, PASSWORD);

        // act + assert
        assertThatThrownBy(() -> authenticateUserUseCase.execute(EMAIL, PASSWORD))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password invalid");

        verifyNoInteractions(tokenProviderAdapterPort, createSessionUseCasePort);
    }
}
