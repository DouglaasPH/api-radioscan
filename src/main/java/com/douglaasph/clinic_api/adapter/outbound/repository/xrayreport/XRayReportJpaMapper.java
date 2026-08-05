package com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJPaMapper;
import com.douglaasph.clinic_api.domain.domain.XRayReport;

public class XRayReportJpaMapper {
    public static XRayReport toDomain(XRayReportJpaEntity entity) {
        return new XRayReport(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getS3Key(),
                entity.getProcessingStatus(),
                entity.getAiResult(),
                entity.getFinalMedicalDiagnosis(),
                entity.isReleasedToPatient(),
                entity.getAppointment().stream().map(AppointmentJPaMapper::toDomain).toList()
        );
    }

    public static XRayReportJpaEntity toEntity(XRayReport xRayReport) {
        return new XRayReportJpaEntity(
                xRayReport.getId(),
                xRayReport.getCreatedAt(),
                xRayReport.getS3Key(),
                xRayReport.getProcessingStatus(),
                xRayReport.getAiResult(),
                xRayReport.getFinalMedicalDiagnosis(),
                xRayReport.isReleasedToPatient(),
                xRayReport.getAppointment().stream().map(AppointmentJPaMapper::toEntity).toList()
        );
    }
}
