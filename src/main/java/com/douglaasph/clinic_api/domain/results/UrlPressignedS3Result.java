package com.douglaasph.clinic_api.domain.results;

public class UrlPressignedS3Result {
    private String urlPreassignedS3;

    public UrlPressignedS3Result() {
    }

    public UrlPressignedS3Result(String urlPreassignedS3) {
        this.urlPreassignedS3 = urlPreassignedS3;
    }

    public String getUrlPreassignedS3() {
        return urlPreassignedS3;
    }

    public void setUrlPreassignedS3(String urlPreassignedS3) {
        this.urlPreassignedS3 = urlPreassignedS3;
    }
}
