package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;

public interface GetDashboardMetricsForAdminUseCasePort {
    DashboardAdminMetricsResult execute();
}
