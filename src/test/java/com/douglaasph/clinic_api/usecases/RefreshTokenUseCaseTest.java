package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetRefreshTokenByTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.TokenProviderAdapterPort;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.usecases.RefreshTokenUseCase;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenUseCaseTest {
    @Mock
    private GetRefreshTokenByTokenAdapterPort getRefreshTokenByTokenAdapterPort;

    @Mock
    private DeleteRefreshTokenAdapterPort deleteRefreshTokenAdapterPort;

    @Mock
    private CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;

    @Mock
    private TokenProviderAdapterPort tokenProviderAdapterPort;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    private static final String TOKEN = "refreshtoken";

    @Test
    @DisplayName("Must return login result when credential valid without expired before token")
    void case1() {
        // arrange
        User user = new User(
                null,
                null,
                "douglas@gmail.com",
                null,
                Roles.PATIENT,
                null,
                null
        );
        RefreshToken refreshToken = new RefreshToken(null, TOKEN, user, Instant.now().plusSeconds(86400));
        when(getRefreshTokenByTokenAdapterPort.findByToken(TOKEN)).thenReturn(Optional.of(refreshToken));
        String accessToken = "accessToken";
        when(tokenProviderAdapterPort.generateToken(refreshToken.getUser().getEmail())).thenReturn(accessToken);

        // act
        LoginResult result = refreshTokenUseCase.execute(TOKEN);

        // assert
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getToken());

        verifyNoInteractions(createRefreshTokenAdapterPort);
        verifyNoInteractions(deleteRefreshTokenAdapterPort);
    }

    @Test
    @DisplayName("Must return login result when credential valid with expired before token")
    void case2() {
        // arrange
        User user = new User(
                null,
                null,
                "douglas@gmail.com",
                null,
                Roles.PATIENT,
                null,
                null
        );
        RefreshToken oldRefreshToken = new RefreshToken(null, TOKEN, user, Instant.now().minusSeconds(86400));
        when(getRefreshTokenByTokenAdapterPort.findByToken(TOKEN)).thenReturn(Optional.of(oldRefreshToken));
        RefreshToken newRefreshToken = new RefreshToken(null, TOKEN, user, Instant.now().plusSeconds(86400));
        when(createRefreshTokenAdapterPort.create(oldRefreshToken.getUser().getEmail())).thenReturn(newRefreshToken);
        String accessToken = "accessToken";
        when(tokenProviderAdapterPort.generateToken(oldRefreshToken.getUser().getEmail())).thenReturn(accessToken);
        // act
        LoginResult result = refreshTokenUseCase.execute(TOKEN);

        // assert
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken.getToken());
    }

    @Test
    @DisplayName("Should propagate exception when refresh token not found")
    void case3() {
        // arrange
        doThrow(new ResourceNotFoundException("RefreshToken", "token", TOKEN))
                .when(getRefreshTokenByTokenAdapterPort).findByToken(TOKEN);

        // act + assert
        assertThatThrownBy(() -> refreshTokenUseCase.execute(TOKEN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("RefreshToken not found with token: 'refreshtoken'");

        verifyNoInteractions(createRefreshTokenAdapterPort);
        verifyNoInteractions(deleteRefreshTokenAdapterPort);
        verifyNoInteractions(tokenProviderAdapterPort);
    }
}
