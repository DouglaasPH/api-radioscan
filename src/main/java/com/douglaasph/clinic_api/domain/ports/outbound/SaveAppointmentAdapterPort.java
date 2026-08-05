package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;

public interface SaveAppointmentAdapterPort {
    Appointment save(Appointment appointment);
}
