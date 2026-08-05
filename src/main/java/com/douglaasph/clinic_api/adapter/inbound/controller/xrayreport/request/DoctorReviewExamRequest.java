package com.douglaasph.clinic_api.adapter.inbound.controller.xrayreport.request;

import jakarta.validation.constraints.NotBlank;

public class DoctorReviewExamRequest {
    @NotBlank(message = "The employee_id cannot be blank.")
    private String finalDoctorDiagnosis;

    public DoctorReviewExamRequest() {}

    public DoctorReviewExamRequest(String finalDoctorDiagnosis) {
        this.finalDoctorDiagnosis = finalDoctorDiagnosis;
    }

    public String getFinalDoctorDiagnosis() {
        return finalDoctorDiagnosis;
    }

    public void setFinalDoctorDiagnosis(String finalDoctorDiagnosis) {
        this.finalDoctorDiagnosis = finalDoctorDiagnosis;
    }
}
