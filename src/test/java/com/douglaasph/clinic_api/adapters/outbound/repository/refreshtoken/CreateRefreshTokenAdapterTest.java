package com.douglaasph.clinic_api.adapters.outbound.repository.refreshtoken;

import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.CreateRefreshTokenAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.refreshtoken.RefreshTokenRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class CreateRefreshTokenAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private CreateRefreshTokenAdapter createRefreshTokenAdapter;

    @BeforeEach
    void setUp() { createRefreshTokenAdapter = new CreateRefreshTokenAdapter(refreshTokenRepository,userRepository); }

    @Test
    @DisplayName("Must create refresh token")
    void case1() {
        UserJpaEntity entity = entityManager.persistAndFlush(
                new UserJpaEntity(
                        null,
                        "Test Patient",
                        "example@example.com",
                        "password123",
                        Roles.PATIENT
                )
        );

        // act
        RefreshToken result = createRefreshTokenAdapter.create("example@example.com");

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Should propagate exception when user not exists")
    void case2() {
        // act + assert
        assertThatThrownBy(() -> createRefreshTokenAdapter.create("example@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }
}
