package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;

public interface BookExamCaptureUseCasePort {
    Appointment execute(Long appointmentId, String patientEmail) throws AppointmentConflictException;
}
