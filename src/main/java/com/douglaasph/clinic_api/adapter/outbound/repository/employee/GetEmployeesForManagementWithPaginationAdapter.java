package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeesForManagementWithPaginationAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetEmployeesForManagementWithPaginationAdapter implements GetEmployeesForManagementWithPaginationAdapterPort {
    private final UserRepository userRepository;

    @Override
    public Page<User> get(String name, Position position, Integer page) {
        PageRequest pageable = PageRequest.of(page, 4);
        Integer positionCode = (position == null) ? null : position.getCode();

        return userRepository.findEmployeesWithFilters(name, positionCode, pageable).map(UserJpaMapper::toDomain);
    }
}
