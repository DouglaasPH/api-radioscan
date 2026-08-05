package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;
import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsForEmployeeDashboardAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class GetMetricsForEmployeeDashboardAdapter implements GetMetricsForEmployeeDashboardAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public EmployeesMetricsResult get(Long userId) {
        return appointmentRepository.getDailyMetrics(
                userId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
    }
}
