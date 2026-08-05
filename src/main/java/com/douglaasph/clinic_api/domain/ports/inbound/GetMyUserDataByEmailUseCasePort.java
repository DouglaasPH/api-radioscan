package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.User;

public interface GetMyUserDataByEmailUseCasePort {
    User execute(String email);
}
