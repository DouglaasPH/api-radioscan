package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.ports.inbound.GetAppointmentByIdUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetAppointmentByIdUseCase implements GetAppointmentByIdUseCasePort {
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;

    public GetAppointmentByIdUseCase(GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort) {
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
    }

    @Override
    public Appointment execute(Long appointmentId) {
        return getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
    }
}
