package com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.domain.domain.XRayReport;

import java.util.List;

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
                List.of()
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
                List.of()
        );
    }
}
