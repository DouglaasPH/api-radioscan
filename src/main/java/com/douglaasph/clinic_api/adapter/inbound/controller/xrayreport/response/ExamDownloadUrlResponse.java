package com.douglaasph.clinic_api.adapter.inbound.controller.xrayreport.response;

public class ExamDownloadUrlResponse {
    private String downloadUrl;

    public ExamDownloadUrlResponse() {
    }

    public ExamDownloadUrlResponse(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
