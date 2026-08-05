package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Patient;

import java.util.Optional;

public interface GetPatientByIdAdapterPort {
    Optional<Patient> findById(Long patientId);
}
