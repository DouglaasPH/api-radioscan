package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.LoginResult;

public interface CreatePatientAccountUseCasePort {
    LoginResult execute(String name, String email, String password, String cpf, String phone);
}
