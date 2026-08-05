package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.inbound.AuthenticateGoogleUserUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateGoogleUserUseCase implements AuthenticateGoogleUserUseCasePort {
    private final GoogleTokenVerifierAdapterPort googleTokenVerifierAdapterPort;
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final TokenProviderAdapterPort tokenProviderAdapterPort;
    private final CreateSessionUseCasePort createSessionUseCasePort;


    public AuthenticateGoogleUserUseCase(GoogleTokenVerifierAdapterPort googleTokenVerifierAdapterPort, GetUserByEmailAdapterPort getUserByEmailAdapterPort, TokenProviderAdapterPort tokenProviderAdapterPort, CreateSessionUseCasePort createSessionUseCasePort) {
        this.googleTokenVerifierAdapterPort = googleTokenVerifierAdapterPort;
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.tokenProviderAdapterPort = tokenProviderAdapterPort;
        this.createSessionUseCasePort = createSessionUseCasePort;
    }

    @Override
    public LoginResult execute(String googleToken) {
        String email = googleTokenVerifierAdapterPort.verifyAndGetEmail(googleToken);

        User user = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String accessToken = tokenProviderAdapterPort.generateToken(user.getEmail());
        String refreshToken = createSessionUseCasePort.execute(email).getToken();

        return new LoginResult(accessToken, refreshToken);
    }
}
