package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;

public interface GetMetricsEmployeesForAdminUseCasePort {
    MetricsEmployeesForAdminResult execute();
}
