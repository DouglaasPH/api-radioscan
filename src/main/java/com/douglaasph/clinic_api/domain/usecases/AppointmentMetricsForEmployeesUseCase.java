package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;
import com.douglaasph.clinic_api.domain.ports.inbound.AppointmentMetricsForEmployeesUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsForEmployeeDashboardAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppointmentMetricsForEmployeesUseCase implements AppointmentMetricsForEmployeesUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final GetMetricsForEmployeeDashboardAdapterPort getMetricsForEmployeeDashboardAdapterPort;

    public AppointmentMetricsForEmployeesUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort, GetMetricsForEmployeeDashboardAdapterPort getMetricsForEmployeeDashboardAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.getMetricsForEmployeeDashboardAdapterPort = getMetricsForEmployeeDashboardAdapterPort;
    }

    @Override
    public EmployeesMetricsResult execute(String email) {
        Long userId = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email)).getId();

        return getMetricsForEmployeeDashboardAdapterPort.get(userId);
    }
}
