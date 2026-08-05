package com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.ports.outbound.GetRefreshTokenByTokenAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetRefreshTokenAdapter implements GetRefreshTokenByTokenAdapterPort {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token).map(RefreshTokenJpaMapper::toDomain);
    }
}
