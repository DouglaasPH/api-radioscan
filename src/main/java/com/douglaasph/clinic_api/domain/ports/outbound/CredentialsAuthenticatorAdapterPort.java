package com.douglaasph.clinic_api.domain.ports.outbound;

public interface CredentialsAuthenticatorAdapterPort {
    void authenticate(String email, String password);
}
