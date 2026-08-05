package com.douglaasph.clinic_api.adapter.outbound.repository.patient;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Patient;

public class PatientJpaMapper {
    public static Patient toDomain(PatientJpaEntity entity) {
        return new Patient(
                entity.getId(),
                entity.getCpf(),
                entity.getPhone(),
                UserJpaMapper.toDomain(entity.getUser())
        );
    }

    public static PatientJpaEntity toEntity(Patient patient) {
        return new PatientJpaEntity(
                patient.getId(),
                patient.getCpf(),
                patient.getPhone(),
                UserJpaMapper.toEntity(patient.getUser())
        );
    }
}
