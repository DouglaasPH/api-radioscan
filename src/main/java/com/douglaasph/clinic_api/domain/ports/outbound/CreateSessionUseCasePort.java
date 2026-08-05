package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;

public interface CreateSessionUseCasePort {
    RefreshToken execute(String email);
}
