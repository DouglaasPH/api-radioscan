package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.XRayReport;

public interface DoctorReviewExamUseCasePort {
    XRayReport execute(Long appointmentId, String finalDoctorDiagnosis);
}
