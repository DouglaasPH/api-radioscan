package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import org.springframework.data.domain.Page;

public interface GetEmployeesForManagementWithPaginationUseCasePort {
    Page<User> execute(String name, Position position, Integer page);
}
