package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.XRayReport;

public interface SaveXRayReportAdapterPort {
    XRayReport save(XRayReport xRayReport);
}
