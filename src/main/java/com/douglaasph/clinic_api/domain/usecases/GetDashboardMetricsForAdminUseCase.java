package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;
import com.douglaasph.clinic_api.domain.ports.inbound.GetDashboardMetricsForAdminUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetDashboardMetricsForAdminAdapterPort;
import org.springframework.stereotype.Service;

@Service
public class GetDashboardMetricsForAdminUseCase implements GetDashboardMetricsForAdminUseCasePort {
    private final GetDashboardMetricsForAdminAdapterPort getDashboardMetricsForAdminAdapterPort;

    public GetDashboardMetricsForAdminUseCase(GetDashboardMetricsForAdminAdapterPort getDashboardMetricsForAdminAdapterPort) {
        this.getDashboardMetricsForAdminAdapterPort = getDashboardMetricsForAdminAdapterPort;
    }

    @Override
    public DashboardAdminMetricsResult execute() {
        return getDashboardMetricsForAdminAdapterPort.get();
    }
}
