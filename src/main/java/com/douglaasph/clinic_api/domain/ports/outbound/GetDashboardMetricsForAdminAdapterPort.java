package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;

public interface GetDashboardMetricsForAdminAdapterPort {
    DashboardAdminMetricsResult get();
}
