package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.inbound.GetAllAvailabilitiesAppointmentsUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAllAvailabilitiesAppointmentsAdapterPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GetAllAvailabilitiesAppointmentsUseCase implements GetAllAvailabilitiesAppointmentsUseCasePort {
    private final GetAllAvailabilitiesAppointmentsAdapterPort getAllAvailabilitiesAppointmentsAdapterPort;

    public GetAllAvailabilitiesAppointmentsUseCase(GetAllAvailabilitiesAppointmentsAdapterPort getAllAvailabilitiesAppointmentsAdapterPort) {
        this.getAllAvailabilitiesAppointmentsAdapterPort = getAllAvailabilitiesAppointmentsAdapterPort;
    }

    @Override
    public Page<AvailabilitiesAppointmentsResult> execute(LocalDate date, AppointmentType appointmentType, Integer page) {
        return getAllAvailabilitiesAppointmentsAdapterPort.get(date, appointmentType, page);
    }
}
