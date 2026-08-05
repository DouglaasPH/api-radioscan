package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.User;

public interface UpdateUserDataUseCasePort {
    User execute(String cpf, String phone, String currentEmail);
}
