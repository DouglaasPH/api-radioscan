package com.douglaasph.clinic_api.domain.results;

import java.time.LocalDateTime;

public class AvailabilitiesAppointmentsResult {
    private Long id;
    private LocalDateTime dateHour;
    private Integer appointmentType;
    private String employeeName;

    public AvailabilitiesAppointmentsResult() {}

    public AvailabilitiesAppointmentsResult(Long id, LocalDateTime dateHour, Integer appointmentType, String employeeName) {
        this.id = id;
        this.dateHour = dateHour;
        this.appointmentType = appointmentType;
        this.employeeName = employeeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public Integer getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(Integer appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
