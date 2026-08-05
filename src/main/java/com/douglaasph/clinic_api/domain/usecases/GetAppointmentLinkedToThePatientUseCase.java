package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;
import com.douglaasph.clinic_api.domain.ports.inbound.GetAppointmentLinkedToThePatientUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsLinkedToThePatientAdapterPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAppointmentLinkedToThePatientUseCase implements GetAppointmentLinkedToThePatientUseCasePort {
    private final GetAppointmentsLinkedToThePatientAdapterPort getAppointmentsLinkedToThePatientAdapterPort;

    public GetAppointmentLinkedToThePatientUseCase(GetAppointmentsLinkedToThePatientAdapterPort getAppointmentsLinkedToThePatientAdapterPort) {
        this.getAppointmentsLinkedToThePatientAdapterPort = getAppointmentsLinkedToThePatientAdapterPort;
    }

    @Override
    public List<AppointmentsLinkedToThePatientResult> execute(String patientEmail) {
        return getAppointmentsLinkedToThePatientAdapterPort.get(patientEmail);
    }
}
