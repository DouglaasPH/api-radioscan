package com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.ports.outbound.GetXRayReportByIdAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetXRayReportByIdAdapter implements GetXRayReportByIdAdapterPort {
    private final XRayReportRepository xRayReportRepository;

    @Override
    public Optional<XRayReport> get(Long xRayReportId) {
        return xRayReportRepository.findById(xRayReportId).map(XRayReportJpaMapper::toDomain);
    }
}
