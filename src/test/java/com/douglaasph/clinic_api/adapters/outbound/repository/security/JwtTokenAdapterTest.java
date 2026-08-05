package com.douglaasph.clinic_api.adapters.outbound.repository.security;

import com.douglaasph.clinic_api.adapter.outbound.security.JwtTokenAdapter;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtTokenAdapterTest {
    private JwtTokenAdapter adapter;

    private static final String TEST_SECRET =
            Base64.getEncoder().encodeToString("test-secret-key-with-at-least-32-bytes!".getBytes());

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(adapter, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(adapter, "expirationInMinutes", 60);
    }

    @Test
    @DisplayName("Must generate token with the correct username")
    void case1() {
        String token = adapter.generateToken("douglas@clinic.com");

        assertThat(token).isNotBlank();
        assertThat(adapter.extractUsername(token)).isEqualTo("douglas@clinic.com");
    }

    @Test
    @DisplayName("Should consider the token valid when the username matches and it has not expired")
    void case2() {
        String token = adapter.generateToken("douglas@clinic.com");

        assertThat(adapter.isTokenValid(token, "douglas@clinic.com")).isTrue();
    }

    @Test
    @DisplayName("Should not consider the token valid when the username does not match")
    void case3() {
        String token = adapter.generateToken("douglas@clinic.com");

        assertThat(adapter.isTokenValid(token, "outro@clinic.com")).isFalse();
    }

    @Test
    @DisplayName("Should not consider the token valid when it has already expired")
    void case4() throws InterruptedException {
        String token = adapter.generateToken("douglas@clinic.com", 0);

        Thread.sleep(50);

        // act + assert
        assertThatThrownBy(() -> adapter.isTokenValid(token, "douglas@clinic.com"))
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("Must generate a token with a custom expiration")
    void case5() {
        String token = adapter.generateToken("douglas@clinic.com", 120);

        assertThat(adapter.isTokenValid(token, "douglas@clinic.com")).isTrue();
    }
}
