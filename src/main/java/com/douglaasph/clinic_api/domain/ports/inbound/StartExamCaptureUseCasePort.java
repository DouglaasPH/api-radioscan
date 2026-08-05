package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.UrlPressignedS3Result;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;

public interface StartExamCaptureUseCasePort {
    UrlPressignedS3Result execute(Long appointmentId, String email) throws AppointmentConflictException;
}
