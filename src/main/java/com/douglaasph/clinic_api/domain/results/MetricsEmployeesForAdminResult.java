package com.douglaasph.clinic_api.domain.results;

public class MetricsEmployeesForAdminResult {
    private Integer numberOfEmplyees;
    private Integer numberOfDoctors;
    private Integer numberOfTechnicians;

    public MetricsEmployeesForAdminResult() {
    }

    public MetricsEmployeesForAdminResult(Integer numberOfEmplyees, Integer numberOfDoctors, Integer numberOfTechnicians) {
        this.numberOfEmplyees = numberOfEmplyees;
        this.numberOfDoctors = numberOfDoctors;
        this.numberOfTechnicians = numberOfTechnicians;
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
