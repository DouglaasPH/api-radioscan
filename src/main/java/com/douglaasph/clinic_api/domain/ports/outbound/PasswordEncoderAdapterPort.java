package com.douglaasph.clinic_api.domain.ports.outbound;

public interface PasswordEncoderAdapterPort {
    boolean matches(String rawPassword, String encodePassword);
    String encode(String rawPassword);
}
