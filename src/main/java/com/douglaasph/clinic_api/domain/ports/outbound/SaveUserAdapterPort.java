package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.User;

public interface SaveUserAdapterPort {
    User save(User user);
}
