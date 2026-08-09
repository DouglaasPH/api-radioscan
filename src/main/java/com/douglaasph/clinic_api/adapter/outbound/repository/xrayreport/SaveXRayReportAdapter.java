package com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveXRayReportAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveXRayReportAdapter implements SaveXRayReportAdapterPort {
    private final XRayReportRepository xRayReportRepository;

    @Override
    public XRayReport save(XRayReport xRayReport) {
        XRayReportJpaEntity xRayReportJpaEntity = new XRayReportJpaEntity(
                xRayReport.getId(),
                xRayReport.getCreatedAt(),
                xRayReport.getS3Key(),
                xRayReport.getProcessingStatus(),
                xRayReport.getAiResult(),
                xRayReport.getFinalMedicalDiagnosis(),
                xRayReport.isReleasedToPatient(),
                List.of()
        );
        return XRayReportJpaMapper.toDomain(xRayReportRepository.save(xRayReportJpaEntity));
    }
}
