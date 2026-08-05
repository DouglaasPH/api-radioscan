package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.LoginResult;

public interface RefreshTokenUseCasePort {
    LoginResult execute(String token);
}
