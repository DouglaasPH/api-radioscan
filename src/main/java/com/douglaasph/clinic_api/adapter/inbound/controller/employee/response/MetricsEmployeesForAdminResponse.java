package com.douglaasph.clinic_api.adapter.inbound.controller.employee.response;

import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;

public class MetricsEmployeesForAdminResponse {
    private Integer numberOfEmplyees;
    private Integer numberOfDoctors;
    private Integer numberOfTechnicians;

    public MetricsEmployeesForAdminResponse() {
    }

    public MetricsEmployeesForAdminResponse(Integer numberOfEmplyees, Integer numberOfDoctors, Integer numberOfTechnicians) {
        this.numberOfEmplyees = numberOfEmplyees;
        this.numberOfDoctors = numberOfDoctors;
        this.numberOfTechnicians = numberOfTechnicians;
    }

    public static MetricsEmployeesForAdminResponse fromDomain(MetricsEmployeesForAdminResult metricsEmployeesForAdminResult) {
        return new MetricsEmployeesForAdminResponse (
                metricsEmployeesForAdminResult.getNumberOfEmplyees(),
                metricsEmployeesForAdminResult.getNumberOfDoctors(),
                metricsEmployeesForAdminResult.getNumberOfTechnicians()
        );
    }

    public Integer getNumberOfEmplyees() {
        return numberOfEmplyees;
    }

    public void setNumberOfEmplyees(Integer numberOfEmplyees) {
        this.numberOfEmplyees = numberOfEmplyees;
    }

    public Integer getNumberOfDoctors() {
        return numberOfDoctors;
    }

    public void setNumberOfDoctors(Integer numberOfDoctors) {
        this.numberOfDoctors = numberOfDoctors;
    }

    public Integer getNumberOfTechnicians() {
        return numberOfTechnicians;
    }

    public void setNumberOfTechnicians(Integer numberOfTechnicians) {
        this.numberOfTechnicians = numberOfTechnicians;
    }
}
