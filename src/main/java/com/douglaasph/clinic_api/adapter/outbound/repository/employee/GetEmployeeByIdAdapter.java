package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeeByIdAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetEmployeeByIdAdapter implements GetEmployeeByIdAdapterPort {
    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<Employee> get(Long employeeId) {
        return employeeRepository.findById(employeeId).map(EmployeeJpaMapper::toDomain);
    }
}
