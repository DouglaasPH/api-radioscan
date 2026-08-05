package com.douglaasph.clinic_api.adapter.inbound.controller.appointment.response;

import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;

public class EmployeesMetricsResponse {
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer pendingAppointments;

    public EmployeesMetricsResponse() {
    }

    public EmployeesMetricsResponse(Integer totalAppointments, Integer completedAppointments, Integer pendingAppointments) {
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.pendingAppointments = pendingAppointments;
    }

    public static EmployeesMetricsResponse fromDomain(EmployeesMetricsResult employeesMetricsResult) {
        return new EmployeesMetricsResponse(
                employeesMetricsResult.getTotalAppointments(),
                employeesMetricsResult.getCompletedAppointments(),
                employeesMetricsResult.getPendingAppointments()
        );
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Integer getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Integer completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public Integer getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(Integer pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }
}
