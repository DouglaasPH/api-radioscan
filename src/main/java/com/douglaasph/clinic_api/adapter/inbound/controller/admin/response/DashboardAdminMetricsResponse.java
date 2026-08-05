package com.douglaasph.clinic_api.adapter.inbound.controller.admin.response;

import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;

public class DashboardAdminMetricsResponse {
    private Integer numberOfExamsPerformed;
    private Integer availableAppointmentSlots;
    private Integer totalNumberOfPatients;

    public DashboardAdminMetricsResponse() {}

    public DashboardAdminMetricsResponse(Integer numberOfExamsPerformed, Integer availableAppointmentSlots, Integer totalNumberOfPatients) {
        this.numberOfExamsPerformed = numberOfExamsPerformed;
        this.availableAppointmentSlots = availableAppointmentSlots;
        this.totalNumberOfPatients = totalNumberOfPatients;
    }

    public static DashboardAdminMetricsResponse fromDomain(DashboardAdminMetricsResult dashboardAdminMetricsResult) {
        return new DashboardAdminMetricsResponse (
                dashboardAdminMetricsResult.getNumberOfExamsPerformed(),
                dashboardAdminMetricsResult.getAvailableAppointmentSlots(),
                dashboardAdminMetricsResult.getTotalNumberOfPatients()
        );
    }

    public Integer getNumberOfExamsPerformed() {
        return numberOfExamsPerformed;
    }

    public void setNumberOfExamsPerformed(Integer numberOfExamsPerformed) {
        this.numberOfExamsPerformed = numberOfExamsPerformed;
    }

    public Integer getAvailableAppointmentSlots() {
        return availableAppointmentSlots;
    }

    public void setAvailableAppointmentSlots(Integer availableAppointmentSlots) {
        this.availableAppointmentSlots = availableAppointmentSlots;
    }

    public Integer getTotalNumberOfPatients() {
        return totalNumberOfPatients;
    }

    public void setTotalNumberOfPatients(Integer totalNumberOfPatients) {
        this.totalNumberOfPatients = totalNumberOfPatients;
    }
}
