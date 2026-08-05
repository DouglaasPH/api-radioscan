package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;

import java.util.List;

public interface GetAppointmentLinkedToThePatientUseCasePort {
    List<AppointmentsLinkedToThePatientResult> execute(String patientEmail);
}
