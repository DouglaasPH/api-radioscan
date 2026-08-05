package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;
import com.douglaasph.clinic_api.domain.ports.inbound.GetMetricsEmployeesForAdminUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsEmployeesForAdminAdapterPort;
import org.springframework.stereotype.Service;

@Service
public class GetMetricsEmployeesForAdminUseCase implements GetMetricsEmployeesForAdminUseCasePort {
    private final GetMetricsEmployeesForAdminAdapterPort getMetricsEmployeesForAdminAdapterPort;

    public GetMetricsEmployeesForAdminUseCase(GetMetricsEmployeesForAdminAdapterPort getMetricsEmployeesForAdminAdapterPort) {
        this.getMetricsEmployeesForAdminAdapterPort = getMetricsEmployeesForAdminAdapterPort;
    }

    @Override
    public MetricsEmployeesForAdminResult execute() {
        return getMetricsEmployeesForAdminAdapterPort.get();
    }
}
