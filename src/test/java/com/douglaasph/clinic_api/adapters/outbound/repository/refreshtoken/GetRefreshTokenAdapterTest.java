package com.douglaasph.clinic_api.adapters.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.GetRefreshTokenAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.RefreshTokenJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.RefreshTokenRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetRefreshTokenAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private GetRefreshTokenAdapter getRefreshTokenAdapter;

    @BeforeEach
    void setUp() { getRefreshTokenAdapter = new GetRefreshTokenAdapter(refreshTokenRepository); }

    @Test
    @DisplayName("Must get refresh token")
    void case1() {
        UserJpaEntity userEntity = entityManager.persistAndFlush(
                new UserJpaEntity(
                        null,
                        "Test Patient",
                        "example@example.com",
                        "password123",
                        Roles.PATIENT
                )
        );
        RefreshTokenJpaEntity refreshEntity = entityManager.persistAndFlush(
                new RefreshTokenJpaEntity(
                        null,
                        "token",
                        userEntity,
                        Instant.now().plusSeconds(86400)
                )
        );

        // act
        Optional<RefreshToken> result = getRefreshTokenAdapter.findByToken("token");

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("token");
    }

    @Test
    @DisplayName("Must get empty")
    void case2() {
        // act
        Optional<RefreshToken> result = getRefreshTokenAdapter.findByToken("token");

        // assert
        assertThat(result).isEmpty();
    }
}
