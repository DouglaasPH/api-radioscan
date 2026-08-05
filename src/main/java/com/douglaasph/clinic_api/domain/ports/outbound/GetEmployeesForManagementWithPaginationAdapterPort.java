package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import org.springframework.data.domain.Page;

public interface GetEmployeesForManagementWithPaginationAdapterPort {
    Page<User> get(String name, Position position, Integer page);
}
