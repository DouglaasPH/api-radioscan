package com.douglaasph.clinic_api.adapter.outbound.repository.patient;

import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.ports.outbound.GetPatientByIdAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetPatientByIdAdapter implements GetPatientByIdAdapterPort {
    private final PatientRepository patientRepository;

    @Override
    public Optional<Patient> findById(Long patientId) {
        return patientRepository.findById(patientId).map(PatientJpaMapper::toDomain);
    }
}
