package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface GetAllAvailabilitiesAppointmentsUseCasePort {
    Page<AvailabilitiesAppointmentsResult> execute(LocalDate date, AppointmentType appointmentType, Integer page);
}
