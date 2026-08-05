package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.User;

import java.util.Optional;

public interface GetUserByEmailAdapterPort {
    Optional<User> findByEmail(String email);
}
