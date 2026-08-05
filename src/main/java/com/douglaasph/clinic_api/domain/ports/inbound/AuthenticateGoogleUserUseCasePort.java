package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.LoginResult;

public interface AuthenticateGoogleUserUseCasePort {
    LoginResult execute(String googleToken);
}
