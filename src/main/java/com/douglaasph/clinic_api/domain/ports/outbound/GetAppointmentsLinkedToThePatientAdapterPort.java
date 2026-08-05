package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;

import java.util.List;

public interface GetAppointmentsLinkedToThePatientAdapterPort {
    List<AppointmentsLinkedToThePatientResult> get(String patientEmail);
}
