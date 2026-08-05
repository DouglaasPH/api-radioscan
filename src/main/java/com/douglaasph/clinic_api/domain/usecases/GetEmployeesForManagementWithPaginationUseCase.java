package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.ports.inbound.GetEmployeesForManagementWithPaginationUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeesForManagementWithPaginationAdapterPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetEmployeesForManagementWithPaginationUseCase implements GetEmployeesForManagementWithPaginationUseCasePort {
    private final GetEmployeesForManagementWithPaginationAdapterPort getEmployeesForManagementWithPaginationAdapterPort;

    public GetEmployeesForManagementWithPaginationUseCase(GetEmployeesForManagementWithPaginationAdapterPort getEmployeesForManagementWithPaginationAdapterPort) {
        this.getEmployeesForManagementWithPaginationAdapterPort = getEmployeesForManagementWithPaginationAdapterPort;
    }

    @Override
    public Page<User> execute(String name, Position position, Integer page) {
        return getEmployeesForManagementWithPaginationAdapterPort.get(name, position, page);
    }
}
