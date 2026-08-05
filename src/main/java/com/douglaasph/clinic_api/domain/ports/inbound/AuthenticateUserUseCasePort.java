package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.LoginResult;

public interface AuthenticateUserUseCasePort {
    LoginResult execute(String email, String password);
}
