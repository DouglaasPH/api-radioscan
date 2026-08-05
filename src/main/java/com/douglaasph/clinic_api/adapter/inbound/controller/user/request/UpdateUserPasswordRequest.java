package com.douglaasph.clinic_api.adapter.inbound.controller.user.request;

public class UpdateUserPasswordRequest {
    private String currentPassword;
    private String newPassword;

    public UpdateUserPasswordRequest() {}

    public UpdateUserPasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
