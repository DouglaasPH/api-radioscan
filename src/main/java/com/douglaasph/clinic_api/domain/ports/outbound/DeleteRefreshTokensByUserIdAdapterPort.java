package com.douglaasph.clinic_api.domain.ports.outbound;

public interface DeleteRefreshTokensByUserIdAdapterPort {
    void deleteByUserId(Long userId);
}
