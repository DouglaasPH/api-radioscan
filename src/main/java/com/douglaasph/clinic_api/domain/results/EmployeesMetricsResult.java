package com.douglaasph.clinic_api.domain.results;

public class EmployeesMetricsResult {
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer pendingAppointments;

    public EmployeesMetricsResult() {
    }

    public EmployeesMetricsResult(Integer totalAppointments, Integer completedAppointments, Integer pendingAppointments) {
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.pendingAppointments = pendingAppointments;
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
