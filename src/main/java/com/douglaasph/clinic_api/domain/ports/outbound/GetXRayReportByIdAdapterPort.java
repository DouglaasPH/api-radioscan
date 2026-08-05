package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.XRayReport;

import java.util.Optional;

public interface GetXRayReportByIdAdapterPort {
    Optional<XRayReport> get(Long xRayReportId);
}
