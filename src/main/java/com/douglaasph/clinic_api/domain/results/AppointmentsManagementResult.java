package com.douglaasph.clinic_api.domain.results;

import java.time.LocalDateTime;

public class AppointmentsManagementResult {
    private Long id;
    private LocalDateTime dateHour;
    private Integer appointmentStatus;
    private Integer appointmentType;
    private String employeeName;
    private String patientName;

    public AppointmentsManagementResult() {
    }

    public AppointmentsManagementResult(Long id, LocalDateTime dateHour, Integer appointmentStatus, Integer appointmentType, String employeeName, String patientName) {
        this.id = id;
        this.dateHour = dateHour;
        this.appointmentStatus = appointmentStatus;
        this.appointmentType = appointmentType;
        this.employeeName = employeeName;
        this.patientName = patientName;
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

    public Integer getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(Integer appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
