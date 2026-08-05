package com.douglaasph.clinic_api.domain.ports.outbound;

public interface DeleteRefreshTokenAdapterPort {
    void delete(String token);
}
