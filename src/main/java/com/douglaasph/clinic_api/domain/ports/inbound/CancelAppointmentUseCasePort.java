package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;

public interface CancelAppointmentUseCasePort {
    Appointment execute(Long appointmentId, String email);
}
