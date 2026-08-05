package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.ports.inbound.AuthenticateUserUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateSessionUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.CredentialsAuthenticatorAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.TokenProviderAdapterPort;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCase implements AuthenticateUserUseCasePort {
    private final CredentialsAuthenticatorAdapterPort credentialsAuthenticatorAdapterPort;
    private final TokenProviderAdapterPort tokenProviderAdapterPort;
    private final CreateSessionUseCasePort createSessionUseCasePort;


    public AuthenticateUserUseCase(CredentialsAuthenticatorAdapterPort credentialsAuthenticatorAdapterPort, TokenProviderAdapterPort tokenProviderAdapterPort, CreateSessionUseCasePort createSessionUseCasePort) {
        this.credentialsAuthenticatorAdapterPort = credentialsAuthenticatorAdapterPort;
        this.tokenProviderAdapterPort = tokenProviderAdapterPort;
        this.createSessionUseCasePort = createSessionUseCasePort;
    }

    @Override
    public LoginResult execute(String email, String password) {
        credentialsAuthenticatorAdapterPort.authenticate(email, password);
        String accessToken = tokenProviderAdapterPort.generateToken(email);
        String refreshToken = createSessionUseCasePort.execute(email).getToken();

        return new LoginResult(accessToken, refreshToken);
    }
}
