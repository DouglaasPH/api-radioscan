package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Patient;

public interface SavePatientAdapterPort {
    Patient save(Patient patient);
}
