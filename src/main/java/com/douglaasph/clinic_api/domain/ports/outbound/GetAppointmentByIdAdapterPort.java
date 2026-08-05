package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;

import java.util.Optional;

public interface GetAppointmentByIdAdapterPort {
    Optional<Appointment> get(Long appointmentId);
}
