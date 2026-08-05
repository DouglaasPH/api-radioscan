package com.douglaasph.clinic_api.adapter.outbound.repository.user;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveUserAdapter implements SaveUserAdapterPort {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        try {
            UserJpaEntity entity = new UserJpaEntity(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getRole()
            );
            return UserJpaMapper.toDomain(userRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Registration error: Email already exist.");
        }
    }
}
