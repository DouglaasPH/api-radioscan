package com.douglaasph.clinic_api.domain.results;

public class DashboardAdminMetricsResult {
    private Integer numberOfExamsPerformed;
    private Integer availableAppointmentSlots;
    private Integer totalNumberOfPatients;

    public DashboardAdminMetricsResult() {}

    public DashboardAdminMetricsResult(Integer numberOfExamsPerformed, Integer availableAppointmentSlots, Integer totalNumberOfPatients) {
        this.numberOfExamsPerformed = numberOfExamsPerformed;
        this.availableAppointmentSlots = availableAppointmentSlots;
        this.totalNumberOfPatients = totalNumberOfPatients;
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
