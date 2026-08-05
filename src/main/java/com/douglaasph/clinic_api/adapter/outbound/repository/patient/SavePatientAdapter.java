package com.douglaasph.clinic_api.adapter.outbound.repository.patient;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.ports.outbound.SavePatientAdapterPort;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavePatientAdapter implements SavePatientAdapterPort {
    private final PatientRepository patientRepository;

    @Override
    public Patient save(Patient patient) {
        try {
            PatientJpaEntity patientJpaEntity = new PatientJpaEntity(
                    patient.getId(),
                    patient.getCpf(),
                    patient.getPhone(),
                    UserJpaMapper.toEntity(patient.getUser())
            );
            return PatientJpaMapper.toDomain(patientRepository.save(patientJpaEntity));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Registration error: CPF already exist.");
        }
    }
}
