package com.douglaasph.clinic_api.adapter.inbound.controller.appointment.response;

import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;

import java.time.LocalDateTime;

public class AppointmentsManagementResponse {
    private Long id;
    private LocalDateTime dateHour;
    private Integer appointmentStatus;
    private Integer appointmentType;
    private String employeeName;
    private String patientName;

    public AppointmentsManagementResponse() {
    }

    public AppointmentsManagementResponse(Long id, LocalDateTime dateHour, Integer appointmentStatus, Integer appointmentType, String employeeName, String patientName) {
        this.id = id;
        this.dateHour = dateHour;
        this.appointmentStatus = appointmentStatus;
        this.appointmentType = appointmentType;
        this.employeeName = employeeName;
        this.patientName = patientName;
    }

    public static AppointmentsManagementResponse fromDomain(AppointmentsManagementResult appointmentsManagementResult) {
        return new AppointmentsManagementResponse(
                appointmentsManagementResult.getId(),
                appointmentsManagementResult.getDateHour(),
                appointmentsManagementResult.getAppointmentStatus(),
                appointmentsManagementResult.getAppointmentType(),
                appointmentsManagementResult.getEmployeeName(),
                appointmentsManagementResult.getPatientName()
        );
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
