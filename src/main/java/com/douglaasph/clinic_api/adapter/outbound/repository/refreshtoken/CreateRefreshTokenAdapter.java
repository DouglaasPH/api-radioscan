package com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateRefreshTokenAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateRefreshTokenAdapter implements CreateRefreshTokenAdapterPort {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public RefreshToken create(String email) {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(("User not found")));

        String newToken = UUID.randomUUID().toString();
        Instant validity = Instant.now().plus(7, ChronoUnit.DAYS);
        RefreshTokenJpaEntity refreshToken = new RefreshTokenJpaEntity(null, newToken, user, validity);
        return RefreshTokenJpaMapper.toDomain(refreshTokenRepository.save(refreshToken));
    }
}
