package com.douglaasph.clinic_api.adapter.inbound.controller.appointment.response;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;

import java.time.LocalDateTime;

public class AppointmentsLinkedToThePatientResponse {
    private Long id;
    private Integer appointmentStatus;
    private Integer appointmentType;
    private String employeeName;
    private LocalDateTime dateHour;
    private Long xRayReportId;
    private Boolean isReleasedToPatientXRayReport; // Boolean -> true, false or null.
    private Boolean isPendingProcessing;

    public AppointmentsLinkedToThePatientResponse() {
    }

    public AppointmentsLinkedToThePatientResponse(Long id, Integer appointmentStatus, Integer appointmentType, String employeeName, LocalDateTime dateHour, Long xRayReportId, Boolean isReleasedToPatientXRayReport, Boolean isPendingProcessing) {
        this.id = id;
        this.appointmentStatus = appointmentStatus;
        this.appointmentType = appointmentType;
        this.employeeName = employeeName;
        this.dateHour = dateHour;
        this.xRayReportId = xRayReportId;
        this.isReleasedToPatientXRayReport = isReleasedToPatientXRayReport;
        this.isPendingProcessing = isPendingProcessing;
    }

    public static AppointmentsLinkedToThePatientResponse fromDomain(AppointmentsLinkedToThePatientResult appointmentsLinkedToThePatientResult) {
        return new AppointmentsLinkedToThePatientResponse(
                appointmentsLinkedToThePatientResult.getId(),
                appointmentsLinkedToThePatientResult.getAppointmentStatus(),
                appointmentsLinkedToThePatientResult.getAppointmentType(),
                appointmentsLinkedToThePatientResult.getEmployeeName(),
                appointmentsLinkedToThePatientResult.getDateHour(),
                appointmentsLinkedToThePatientResult.getxRayReportId(),
                appointmentsLinkedToThePatientResult.getReleasedToPatientXRayReport(),
                appointmentsLinkedToThePatientResult.getPendingProcessing()
                );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public Long getxRayReportId() {
        return xRayReportId;
    }

    public void setxRayReportId(Long xRayReportId) {
        this.xRayReportId = xRayReportId;
    }

    public Boolean getReleasedToPatientXRayReport() {
        return isReleasedToPatientXRayReport;
    }

    public void setReleasedToPatientXRayReport(Boolean releasedToPatientXRayReport) {
        isReleasedToPatientXRayReport = releasedToPatientXRayReport;
    }

    public Boolean getPendingProcessing() {
        return isPendingProcessing;
    }

    public void setPendingProcessing(Boolean pendingProcessing) {
        isPendingProcessing = pendingProcessing;
    }
}
