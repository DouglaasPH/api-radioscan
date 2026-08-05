package com.douglaasph.clinic_api.adapters.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.DeleteRefreshTokenAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.RefreshTokenJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.RefreshTokenRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class DeleteRefreshTokenAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private DeleteRefreshTokenAdapter deleteRefreshTokenAdapter;

    @BeforeEach
    void setUp() { deleteRefreshTokenAdapter = new DeleteRefreshTokenAdapter(refreshTokenRepository); }

    @Test
    @DisplayName("Must delete refresh token")
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

        Long savedTokenId = refreshEntity.getId();

        // act
        deleteRefreshTokenAdapter.delete("token");
        entityManager.flush();

        // assert
        Optional<RefreshTokenJpaEntity> foundToken = refreshTokenRepository.findByToken("token");
        assertThat(foundToken).isEmpty();

        RefreshTokenJpaEntity entityInDb = entityManager.find(RefreshTokenJpaEntity.class, savedTokenId);
        assertThat(entityInDb).isNull();
    }

    @Test
    @DisplayName("Should propagate exception when refresh token not exists")
    void case2() {
        assertThatThrownBy(() -> deleteRefreshTokenAdapter.delete("example"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("RefreshToken not found with token: 'example'");

        assertThat(refreshTokenRepository.findByToken("example")).isEmpty();
    }
}
