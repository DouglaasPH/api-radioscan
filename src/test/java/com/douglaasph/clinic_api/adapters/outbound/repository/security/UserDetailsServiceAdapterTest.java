package com.douglaasph.clinic_api.adapters.outbound.repository.security;

import com.douglaasph.clinic_api.adapter.outbound.security.UserDetailsServiceAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceAdapterTest {
    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceAdapter userDetailsServiceAdapter;

    @Test
    @DisplayName("Should load UserDetails when user exists")
    void case1() {
        userDetailsServiceAdapter = new UserDetailsServiceAdapter(userRepository);

        UserJpaEntity entity = new UserJpaEntity(1L, "Douglas", "douglas@clinic.com", "hash123", Roles.ADMIN);
        when(userRepository.findByEmail("douglas@clinic.com")).thenReturn(Optional.of(entity));

        UserDetails result = userDetailsServiceAdapter.loadUserByUsername("douglas@clinic.com");

        assertThat(result.getUsername()).isEqualTo("douglas@clinic.com");
        assertThat(result.getPassword()).isEqualTo("hash123");
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should throw UsernameNotFound when the user does not exist")
    void case2() {
        userDetailsServiceAdapter = new UserDetailsServiceAdapter(userRepository);

        when(userRepository.findByEmail("naoexiste@clinic.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsServiceAdapter.loadUserByUsername("naoexiste@clinic.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
