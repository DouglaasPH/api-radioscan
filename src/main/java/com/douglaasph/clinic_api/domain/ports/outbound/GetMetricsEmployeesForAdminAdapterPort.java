package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;

public interface GetMetricsEmployeesForAdminAdapterPort {
    MetricsEmployeesForAdminResult get();
}
