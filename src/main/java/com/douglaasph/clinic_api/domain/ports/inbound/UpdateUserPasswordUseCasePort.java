package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.User;

public interface UpdateUserPasswordUseCasePort {
    User execute(String email, String currentPassword, String newPassword);
}
