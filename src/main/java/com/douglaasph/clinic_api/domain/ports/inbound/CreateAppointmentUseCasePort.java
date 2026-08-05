package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;

import java.time.LocalDateTime;

public interface CreateAppointmentUseCasePort {
    Appointment execute(Long employee_id, LocalDateTime dateHour, AppointmentType type);
}
