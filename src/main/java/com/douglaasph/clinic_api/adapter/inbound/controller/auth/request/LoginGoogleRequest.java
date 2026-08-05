package com.douglaasph.clinic_api.adapter.inbound.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginGoogleRequest {
    @NotBlank
    @NotNull
    String googleToken;

    public LoginGoogleRequest(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }
}
