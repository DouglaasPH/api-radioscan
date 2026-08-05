package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;

import java.util.Optional;

public interface GetRefreshTokenByTokenAdapterPort {
    Optional<RefreshToken> findByToken(String token);
}
