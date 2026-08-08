package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.domain.domain.Employee;

public class EmployeeJpaMapper {
    public static Employee toDomain(EmployeeJpaEntity entity) {
        return new Employee(
                entity.getId(),
                entity.getLicenseNumber(),
                entity.getPosition(),
                null
        );
    }

    public static EmployeeJpaEntity toEntity(Employee employee) {
        return new EmployeeJpaEntity(
                employee.getId(),
                employee.getLicenseNumber(),
                employee.getPosition(),
                null
        );
    }
}
