package com.douglaasph.clinic_api.services;

import com.douglaasph.clinic_api.controllers.dto.admin.DashboardAdminMetricsDto;
import com.douglaasph.clinic_api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AdminService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    public DashboardAdminMetricsDto dashboardMetrics() {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        LocalDate today = LocalDate.now(zoneId);
        Instant startOfDay = today.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = today.atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant();

        Integer numberOfExamsPerformed = this.xRayReportRepository.findByCreatedAtBetween(startOfDay, endOfDay).size();
        Integer availableAppointmentSlots = this.appointmentRepository.findByAppointmentStatusAndDateHourAfter(1, LocalDateTime.now()).size();
        Integer totalNumberOfPatients = this.patientRepository.findAll().size();

        return new DashboardAdminMetricsDto(numberOfExamsPerformed, availableAppointmentSlots, totalNumberOfPatients);
    }
}
