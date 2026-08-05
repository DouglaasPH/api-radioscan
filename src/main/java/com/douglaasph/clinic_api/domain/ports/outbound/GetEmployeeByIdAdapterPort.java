package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Employee;

import java.util.Optional;

public interface GetEmployeeByIdAdapterPort {
    Optional<Employee> get(Long employeeId);
}
