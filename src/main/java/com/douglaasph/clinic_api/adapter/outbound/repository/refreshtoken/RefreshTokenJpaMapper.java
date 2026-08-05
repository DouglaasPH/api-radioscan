package com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;

public class RefreshTokenJpaMapper {
    public static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getToken(),
                UserJpaMapper.toDomain(entity.getUser()),
                entity.getExpiryDate()
        );
    }

    public static RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getId(),
                refreshToken.getToken(),
                UserJpaMapper.toEntity(refreshToken.getUser()),
                refreshToken.getExpiryDate()
        );
    }
}
