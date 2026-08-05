package com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokensByUserIdAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteRefreshTokensByUserIdAdapter implements DeleteRefreshTokensByUserIdAdapterPort {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.flush();
    }
}
