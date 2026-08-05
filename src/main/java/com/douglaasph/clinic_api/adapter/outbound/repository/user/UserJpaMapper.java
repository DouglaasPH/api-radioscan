package com.douglaasph.clinic_api.adapter.outbound.repository.user;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaMapper;
import com.douglaasph.clinic_api.domain.domain.User;

public class UserJpaMapper {
    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getEmployee() != null ? EmployeeJpaMapper.toDomain(entity.getEmployee()) : null,
                entity.getPatient() != null ? PatientJpaMapper.toDomain(entity.getPatient()) : null
        );
    }

    public static UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        // employee/patient as needed
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
