package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveEmployeeAdapterPort;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveEmployeeAdapter implements SaveEmployeeAdapterPort {
    private final EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee employee) {
        try {
            EmployeeJpaEntity entity = new EmployeeJpaEntity(
                    employee.getId(),
                    employee.getLicenseNumber(),
                    employee.getPosition(),
                    UserJpaMapper.toEntity(employee.getUser())
            );
            return EmployeeJpaMapper.toDomain(employeeRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Registration error: license number already exist.");
        }
    }
}
