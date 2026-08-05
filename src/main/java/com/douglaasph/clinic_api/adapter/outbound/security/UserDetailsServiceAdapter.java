package com.douglaasph.clinic_api.adapter.outbound.security;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.config.security.UserPrincipal;
import com.douglaasph.clinic_api.domain.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceAdapter implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrincipal(
                UserJpaMapper.toDomain(user)
        );
    }
}
