package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;

public interface AppointmentMetricsForEmployeesUseCasePort {
    EmployeesMetricsResult execute(String email);
}
