package com.douglaasph.clinic_api.domain.results;

import java.time.LocalDateTime;

public class AppointmentsLinkedToThePatientResult {
    private Long id;
    private Integer appointmentStatus;
    private Integer appointmentType;
    private String employeeName;
    private LocalDateTime dateHour;
    private Long xRayReportId;
    private Boolean isReleasedToPatientXRayReport; // Boolean -> true, false or null.
    private Boolean isPendingProcessing;

    private String finalMedicalDiagnosis;

    public AppointmentsLinkedToThePatientResult() {
    }

    public AppointmentsLinkedToThePatientResult(Long id, Integer appointmentStatus, Integer appointmentType, String employeeName, LocalDateTime dateHour, Long xRayReportId, Boolean isReleasedToPatientXRayReport, Boolean isPendingProcessing, String finalMedicalDiagnosis) {
        this.id = id;
        this.appointmentStatus = appointmentStatus;
        this.appointmentType = appointmentType;
        this.employeeName = employeeName;
        this.dateHour = dateHour;
        this.xRayReportId = xRayReportId;
        this.isReleasedToPatientXRayReport = isReleasedToPatientXRayReport;
        this.isPendingProcessing = isPendingProcessing;
        this.finalMedicalDiagnosis = finalMedicalDiagnosis;
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

    public Long getXRayReportId() {
        return xRayReportId;
    }

    public void setXRayReportId(Long xRayReportId) {
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

    public String getFinalMedicalDiagnosis() {
        return finalMedicalDiagnosis;
    }

    public void setFinalMedicalDiagnosis(String finalMedicalDiagnosis) {
        this.finalMedicalDiagnosis = finalMedicalDiagnosis;
    }
}
