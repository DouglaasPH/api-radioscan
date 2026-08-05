package com.douglaasph.clinic_api.domain.domain;

import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaEntity;

import java.time.Instant;
import java.util.List;

public class XRayReport {
    private Long id;
    private Instant createdAt;
    private String s3Key;
    private Integer processingStatus;
    private String aiResult;
    private String finalMedicalDiagnosis;
    private boolean releasedToPatient = false;
    private List<Appointment> appointment;

    public XRayReport() {}

    public XRayReport(Long id, Instant createdAt, String s3Key, Integer processingStatus, String aiResult, String finalMedicalDiagnosis, boolean releasedToPatient, List<Appointment> appointment) {
        this.id = id;
        this.s3Key = s3Key;
        this.processingStatus = processingStatus;
        this.aiResult = aiResult;
        this.finalMedicalDiagnosis = finalMedicalDiagnosis;
        this.releasedToPatient = releasedToPatient;
        this.appointment = appointment;

        if (createdAt == null) this.createdAt = Instant.now();
        else this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getAiResult() {
        return aiResult;
    }

    public void setAiResult(String aiResult) {
        this.aiResult = aiResult;
    }

    public String getFinalMedicalDiagnosis() {
        return finalMedicalDiagnosis;
    }

    public void setFinalMedicalDiagnosis(String finalMedicalDiagnosis) {
        this.finalMedicalDiagnosis = finalMedicalDiagnosis;
    }

    public boolean isReleasedToPatient() {
        return releasedToPatient;
    }

    public void setReleasedToPatient(boolean releasedToPatient) {
        this.releasedToPatient = releasedToPatient;
    }

    public List<Appointment> getAppointment() {
        return appointment;
    }

    public void setAppointment(List<Appointment> appointment) {
        this.appointment = appointment;
    }
}
