package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;

public interface GetAppointmentByIdUseCasePort {
    Appointment execute(Long appointmentId);
}
