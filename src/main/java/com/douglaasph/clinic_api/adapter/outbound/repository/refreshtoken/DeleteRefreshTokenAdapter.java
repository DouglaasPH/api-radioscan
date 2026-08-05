package com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteRefreshTokenAdapter implements DeleteRefreshTokenAdapterPort {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void delete(String token) {
        RefreshTokenJpaEntity entity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token", token));
        refreshTokenRepository.delete(entity);
    }
}
