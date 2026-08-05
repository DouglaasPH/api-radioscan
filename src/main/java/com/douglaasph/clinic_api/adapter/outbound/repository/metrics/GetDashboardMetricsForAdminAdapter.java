package com.douglaasph.clinic_api.adapter.outbound.repository.metrics;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportRepository;
import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;
import com.douglaasph.clinic_api.domain.ports.outbound.GetDashboardMetricsForAdminAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class GetDashboardMetricsForAdminAdapter implements GetDashboardMetricsForAdminAdapterPort {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final XRayReportRepository xRayReportRepository;

    @Override
    public DashboardAdminMetricsResult get() {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        LocalDate today = LocalDate.now(zoneId);
        Instant startOfDay = today.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = today.atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant();

        Integer numberOfExamsPerformed = this.xRayReportRepository.findByCreatedAtBetween(startOfDay, endOfDay).size();
        Integer availableAppointmentSlots = this.appointmentRepository.findByAppointmentStatusAndDateHourAfter(1, LocalDateTime.now()).size();
        Integer totalNumberOfPatients = this.patientRepository.findAll().size();

        return new DashboardAdminMetricsResult(numberOfExamsPerformed, availableAppointmentSlots, totalNumberOfPatients);
    }
}
