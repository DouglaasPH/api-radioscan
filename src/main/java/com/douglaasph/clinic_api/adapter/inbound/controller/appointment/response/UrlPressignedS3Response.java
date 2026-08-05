package com.douglaasph.clinic_api.adapter.inbound.controller.appointment.response;

import com.douglaasph.clinic_api.domain.results.UrlPressignedS3Result;

public class UrlPressignedS3Response {
    private String urlPreassignedS3;

    public UrlPressignedS3Response() {
    }

    public UrlPressignedS3Response(String urlPreassignedS3) {
        this.urlPreassignedS3 = urlPreassignedS3;
    }

    public static UrlPressignedS3Response fromDomain(UrlPressignedS3Result urlPressignedS3Result) {
        return new UrlPressignedS3Response(
                urlPressignedS3Result.getUrlPreassignedS3()
        );
    }

    public String getUrlPreassignedS3() {
        return urlPreassignedS3;
    }

    public void setUrlPreassignedS3(String urlPreassignedS3) {
        this.urlPreassignedS3 = urlPreassignedS3;
    }
}
