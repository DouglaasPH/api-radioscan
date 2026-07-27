package com.douglaasph.clinic_api.controllers.dto.appointment;

public record DashboardMetricsForEmployeeResponseDto (Long totalAppointments, Long completedAppointments, Long pendingAppointments) {}
