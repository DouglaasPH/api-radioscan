package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.ports.inbound.RefreshTokenUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetRefreshTokenByTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.TokenProviderAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenUseCase implements RefreshTokenUseCasePort {
    private final GetRefreshTokenByTokenAdapterPort getRefreshTokenByTokenAdapterPort;
    private final DeleteRefreshTokenAdapterPort deleteRefreshTokenAdapterPort;
    private final CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;
    private final TokenProviderAdapterPort tokenProviderAdapterPort;

    public RefreshTokenUseCase(GetRefreshTokenByTokenAdapterPort getRefreshTokenByTokenAdapterPort, DeleteRefreshTokenAdapterPort deleteRefreshTokenAdapterPort, CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort, TokenProviderAdapterPort tokenProviderAdapterPort) {
        this.getRefreshTokenByTokenAdapterPort = getRefreshTokenByTokenAdapterPort;
        this.deleteRefreshTokenAdapterPort = deleteRefreshTokenAdapterPort;
        this.createRefreshTokenAdapterPort = createRefreshTokenAdapterPort;
        this.tokenProviderAdapterPort = tokenProviderAdapterPort;
    }

    @Override
    public LoginResult execute(String token) {
        RefreshToken refreshToken = getRefreshTokenByTokenAdapterPort.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token", token));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            RefreshToken oldToken = refreshToken;
            refreshToken = createRefreshTokenAdapterPort.create(refreshToken.getUser().getEmail());
            deleteRefreshTokenAdapterPort.delete(oldToken.getToken());
        }

        String accessToken = tokenProviderAdapterPort.generateToken(refreshToken.getUser().getEmail());

        return new LoginResult(accessToken, refreshToken.getToken());
    }
}
