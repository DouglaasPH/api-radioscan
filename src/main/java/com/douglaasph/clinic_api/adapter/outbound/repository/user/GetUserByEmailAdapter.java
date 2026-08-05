package com.douglaasph.clinic_api.adapter.outbound.repository.user;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetUserByEmailAdapter implements GetUserByEmailAdapterPort {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserJpaMapper::toDomain);
    }
}
