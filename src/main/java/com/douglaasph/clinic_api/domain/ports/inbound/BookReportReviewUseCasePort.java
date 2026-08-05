package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;

public interface BookReportReviewUseCasePort {
    Appointment execute(Long appointmentId, Long xRayReportId, String patientEmail) throws AppointmentConflictException;
}
